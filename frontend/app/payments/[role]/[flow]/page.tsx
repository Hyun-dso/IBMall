// /app/payments/guest/single/page.tsx
'use client';

import { useSearchParams, useRouter } from 'next/navigation';
import { useEffect, useMemo, useRef, useState } from 'react';
import type { ProductLineItem } from '@/types/cart';
import type { GuestPaymentForm } from '@/types/forms';
import type { OrderIntentPayload } from '@/types/payment'; // intent 타입은 /types로 분리
import ProductLineItemCard from '@/components/ProductLineItemCard';
import Button from '@/components/ui/Button';
import PortOne, { isPortOneError } from '@portone/browser-sdk/v2';
import { validate } from '@/lib/validators/engine';
import { showToast } from '@/lib/toast';

const CARD_CLASS =
    'border border-border dark:border-dark-border rounded-lg bg-surface dark:bg-dark-surface shadow-sm';
const INPUT_CLASS =
    'w-full px-4 py-3 bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none';

const currency = new Intl.NumberFormat('ko-KR');

function genPaymentId() {
    const parts = [...crypto.getRandomValues(new Uint32Array(2))].map((w) =>
        w.toString(16).padStart(8, '0')
    );
    return parts.join('');
}

// 의도 검증 API 호출: 실제 경로/스키마는 백엔드 명세로 확정 필요
async function verifyOrderIntent(intentId: string) {
    const base = process.env.NEXT_PUBLIC_API_BASE_URL;
    const res = await fetch(`${base}/api/orders/intent/verify?intent=${encodeURIComponent(intentId)}`, {
        method: 'GET',
        credentials: 'include',
        cache: 'no-store',
    });
    if (!res.ok) {
        let msg = '의도 검증 실패';
        try {
            const j = await res.json();
            msg = j.message || msg;
        } catch { }
        throw new Error(msg);
    }
    // 서버는 OrderIntentPayload와 결제용 표시 이름(orderName)을 반환한다고 가정
    return res.json() as Promise<{
        intent: OrderIntentPayload;
        orderName: string;           // 예: "[단일] 상품명 옵션명 x수량"
        product: {
            productId: number;
            name: string;
            thumbnailUrl: string | null;
            optionName: string | null;
        };
    }>;
}

export default function GuestPaymentPage() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const intentId = searchParams.get('intent'); // productId는 더 이상 사용하지 않음

    const [submitting, setSubmitting] = useState(false);
    const [loading, setLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState<string | null>(null);

    const [form, setForm] = useState<GuestPaymentForm>({
        buyerName: '',
        buyerEmail: '',
        buyerPhone: '',
        address1: '',
        address2: '',
        sendToOther: false,
        recipientName: '',
        recipientPhone: '',
    });

    const [product, setProduct] = useState<ProductLineItem | null>(null);
    const [intent, setIntent] = useState<OrderIntentPayload | null>(null);
    const [orderName, setOrderName] = useState<string>('');

    const detailRef = useRef<HTMLInputElement>(null);
    const [postcodeLoaded, setPostcodeLoaded] = useState(false);
    const [popupOpened, setPopupOpened] = useState(false);

    // 우편번호 스크립트 로딩
    const ensurePostcodeScript = async () => {
        if (postcodeLoaded) return;
        if ((window as any).daum?.Postcode) {
            setPostcodeLoaded(true);
            return;
        }
        await new Promise<void>((resolve) => {
            const script = document.createElement('script');
            script.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
            script.onload = () => resolve();
            document.body.appendChild(script);
        });
        setPostcodeLoaded(true);
    };

    const openPostcodePopup = async () => {
        if (popupOpened) return;
        await ensurePostcodeScript();
        setPopupOpened(true);
        new (window as any).daum.Postcode({
            oncomplete: (data: any) => {
                setForm((prev) => ({ ...prev, address1: data.address as string }));
                setPopupOpened(false);
                setTimeout(() => detailRef.current?.focus(), 0);
            },
            onclose: () => setPopupOpened(false),
        }).open();
    };

    // 의도 검증 → 안전한 결제 소스 구성
    useEffect(() => {
        if (!intentId) {
            setErrorMsg('잘못된 접근입니다. 의도 토큰이 없습니다.');
            return;
        }
        (async () => {
            try {
                setLoading(true);
                const { intent: iv, orderName: oname, product: p } = await verifyOrderIntent(intentId);

                // intent 기반으로 단일 아이템 카드 구성. 수량/가격은 서버가 고정/검증.
                const unit = iv.unitPrice;
                const qty = iv.quantity;
                const paidAmount = unit * qty;

                const item: ProductLineItem = {
                    productId: p.productId,
                    name: p.name,
                    price: unit,                 // 단일 기준 가격을 price로 세팅
                    timeSalePrice: null,         // 의도 기준이면 더 이상 개별 타임세일 계산 필요 없음
                    isTimeSale: false,           // 서버가 unitPrice에 반영했다고 가정
                    thumbnailUrl: p.thumbnailUrl ?? null,
                    quantity: qty,
                    paidAmount,
                    optionName: p.optionName ?? null,
                    productOptionId: iv.optionId ?? null,
                    disableQuantityControls: true, // 의도 고정. 조작 불가
                    showDeleteButton: false,
                };

                setIntent(iv);
                setOrderName(oname);
                setProduct(item);
            } catch (e: any) {
                setErrorMsg(e?.message || '의도 검증 중 오류가 발생했습니다.');
            } finally {
                setLoading(false);
            }
        })();
    }, [intentId]);

    const setField = <K extends keyof GuestPaymentForm>(k: K, v: GuestPaymentForm[K]) =>
        setForm((prev) => ({ ...prev, [k]: v }));

    const amount = useMemo(() => {
        if (!product) return 0;
        return product.price * product.quantity; // 이미 intent에서 고정됨
    }, [product]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!intent || !product) return;
        if (amount <= 0) {
            showToast.error('유효하지 않은 결제 금액입니다.', { group: 'payment' });
            return;
        }

        const { ok, errors } = validate('guestPayment', form);
        if (!ok) {
            const first = Object.values(errors)[0];
            if (first) showToast.error(first, { group: 'payment' });
            return;
        }

        const storeId = process.env.NEXT_PUBLIC_PORTONE_V2_STORE_ID;
        const channelKey = process.env.NEXT_PUBLIC_PORTONE_V2_CHANNEL_KEY;
        if (!storeId || !channelKey) {
            showToast.error('결제 설정이 누락되었습니다.', { group: 'payment' });
            return;
        }

        try {
            if (submitting) return;
            setSubmitting(true);

            // 결제 요청: 금액과 주문명은 intent에서 파생된 안전한 값 사용
            const payment = await PortOne.requestPayment({
                storeId,
                channelKey,
                paymentId: genPaymentId(),
                orderName,
                totalAmount: amount,
                currency: 'CURRENCY_KRW',
                payMethod: 'CARD',
                customer: {
                    fullName: form.buyerName,
                    email: form.buyerEmail,
                    phoneNumber: form.buyerPhone,
                },
            });

            if (!payment) {
                showToast.error('결제가 취소되었습니다.', { group: 'payment' });
                return;
            }
            if (isPortOneError(payment)) {
                showToast.error(payment.pgMessage || payment.message || '결제 실패', { group: 'payment' });
                return;
            }

            // 서버 검증 및 주문 저장: 서버는 client 가격/상품 식별자를 신뢰하지 말고 intent 기준으로 재계산
            try {
                const res = await fetch('/api/payments/guest/single', {
                    method: 'POST',
                    credentials: 'include',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        // 결제 식별
                        orderUid: payment.paymentId,
                        paymentId: payment.paymentId,

                        // 서버용 의도 토큰
                        intentId: intent.intentId, // 서버가 필수로 검증해야 함

                        // 레거시/호환 필드(서버는 무시 가능, 최종은 intent로 산정)
                        productId: product.productId,
                        productOptionId: product.productOptionId,
                        productName: product.name,
                        orderPrice: product.price,
                        paidAmount: amount,
                        quantity: product.quantity,

                        // 구매자/수령인
                        buyerName: form.buyerName,
                        buyerEmail: form.buyerEmail,
                        buyerPhone: form.buyerPhone,
                        recipientName: form.sendToOther ? form.recipientName : form.buyerName,
                        recipientPhone: form.sendToOther ? form.recipientPhone : form.buyerPhone,
                        recipientAddress: `${form.address1} ${form.address2}`.trim(),
                    }),
                });

                if (!res.ok) {
                    let msg = '결제 검증 실패';
                    try {
                        const j = await res.json();
                        msg = j.message || msg;
                    } catch { }
                    showToast.error(msg, { group: 'payment' });
                    return;
                }
            } catch {
                showToast.error('결제 검증 요청 실패', { group: 'payment' });
                return;
            }

            showToast.success('결제가 완료되었습니다.', { group: 'payment', persist: true, showNow: false });
            router.push(`/orders/complete?paymentId=${payment.paymentId}`);
        } catch {
            showToast.error('결제 요청 중 오류가 발생했습니다.', { group: 'payment' });
        } finally {
            setSubmitting(false);
        }
    };

    // 의도 누락/오류 대체 UI
    if (!intentId) {
        return (
            <main className="min-h-screen flex items-center justify-center px-4 py-8 bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
                <div className={`p-6 ${CARD_CLASS}`}>잘못된 접근입니다.</div>
            </main>
        );
    }
    if (errorMsg) {
        return (
            <main className="min-h-screen flex items-center justify-center px-4 py-8 bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
                <div className={`p-6 ${CARD_CLASS}`}>
                    <div className="mb-4 text-error">{errorMsg}</div>
                    <div className="flex gap-2">
                        <Button variant="outline" onClick={() => router.push('/')}>메인으로</Button>
                        <Button onClick={() => router.refresh()}>다시 시도</Button>
                    </div>
                </div>
            </main>
        );
    }

    return (
        <main className="min-h-screen flex flex-col items-center justify-start px-4 py-8 bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
            <div className="w-full max-w-md mx-auto space-y-8">
                <div className={`p-6 ${CARD_CLASS}`}>
                    <h2 className="text-lg font-semibold mb-3">주문 상품</h2>
                    {loading ? (
                        <div className="text-text-secondary dark:text-dark-text-secondary">상품 정보를 불러오는 중...</div>
                    ) : product ? (
                        <ProductLineItemCard item={product} onQuantityChange={() => { /* 단일 결제는 수량 고정 */ }} />
                    ) : (
                        <div className="text-error">상품을 찾을 수 없습니다.</div>
                    )}
                </div>

                <form noValidate onSubmit={handleSubmit} className={`w-full max-w-md p-6 ${CARD_CLASS}`}>
                    <h1 className="text-2xl font-bold mb-6 text-center">게스트 결제</h1>

                    <div className="mb-6">
                        <h2 className="text-lg font-semibold mb-2">구매자 정보</h2>
                        <div className="mb-6 border border-border dark:border-dark-border rounded-md overflow-hidden bg-surface dark:bg-dark-surface">
                            <input
                                name="buyerName"
                                placeholder="이름"
                                value={form.buyerName}
                                onChange={(e) => setField('buyerName', e.target.value)}
                                required
                                className={`${INPUT_CLASS} border-b border-border dark:border-dark-border`}
                            />
                            <input
                                name="buyerEmail"
                                type="email"
                                placeholder="이메일"
                                value={form.buyerEmail}
                                onChange={(e) => setField('buyerEmail', e.target.value)}
                                required
                                className={`${INPUT_CLASS} border-b border-border dark:border-dark-border`}
                            />
                            <input
                                name="buyerPhone"
                                placeholder="전화번호"
                                value={form.buyerPhone}
                                onChange={(e) => setField('buyerPhone', e.target.value)}
                                required
                                className={INPUT_CLASS}
                            />
                        </div>
                    </div>

                    <div className="flex items-center justify-between mb-2">
                        <h2 className="text-lg font-semibold">배송지</h2>
                        <Switch
                            checked={form.sendToOther}
                            onChange={(v) => setField('sendToOther', v)}
                            label="다른 사람에게 보내기"
                        />
                    </div>

                    <div className="mb-6 border border-border dark:border-dark-border rounded-md overflow-hidden bg-surface dark:bg-dark-surface">
                        <input
                            name="address1"
                            placeholder="주소"
                            value={form.address1}
                            readOnly
                            onClick={openPostcodePopup}
                            onFocus={openPostcodePopup}
                            required
                            className={`${INPUT_CLASS} border-b border-border dark:border-dark-border cursor-pointer`}
                        />
                        <input
                            name="address2"
                            placeholder="상세주소"
                            value={form.address2}
                            ref={detailRef}
                            onChange={(e) => setField('address2', e.target.value)}
                            required
                            className={`${INPUT_CLASS} ${form.sendToOther ? 'border-b border-border dark:border-dark-border' : ''}`}
                        />
                        {form.sendToOther && (
                            <>
                                <input
                                    name="recipientName"
                                    placeholder="수령인 이름"
                                    value={form.recipientName ?? ''}
                                    onChange={(e) => setField('recipientName', e.target.value)}
                                    required
                                    className={`${INPUT_CLASS} border-b border-border dark:border-dark-border`}
                                />
                                <input
                                    name="recipientPhone"
                                    placeholder="수령인 연락처"
                                    value={form.recipientPhone ?? ''}
                                    onChange={(e) => setField('recipientPhone', e.target.value)}
                                    required
                                    className={INPUT_CLASS}
                                />
                            </>
                        )}
                    </div>

                    <div className="mb-4 flex items-center justify-between">
                        <span className="text-text-secondary dark:text-dark-text-secondary">결제 금액</span>
                        <span className="text-xl font-semibold">{currency.format(amount)}원</span>
                    </div>

                    <Button type="submit" className="w-full py-3 font-bold" disabled={submitting || loading || !product}>
                        {submitting ? '처리 중...' : '결제 진행'}
                    </Button>
                </form>
            </div>
        </main>
    );
}

/* 내부 컴포넌트: 토글 스위치 */
function Switch({
    checked,
    onChange,
    label,
}: {
    checked: boolean;
    onChange: (next: boolean) => void;
    label: string;
}) {
    return (
        <button
            type="button"
            role="switch"
            aria-checked={checked}
            onClick={() => onChange(!checked)}
            className="flex items-center gap-2 hover:cursor-pointer"
        >
            <span className="text-sm text-text-secondary dark:text-dark-text-secondary">{label}</span>
            <span
                className={[
                    'inline-flex h-6 w-11 items-center rounded-full transition',
                    checked ? 'bg-primary hover:bg-accent' : 'bg-border dark:bg-dark-border',
                ].join(' ')}
            >
                <span
                    className={[
                        'inline-block h-5 w-5 rounded-full bg-background dark:bg-dark-surface transform transition',
                        checked ? 'translate-x-5' : 'translate-x-1',
                    ].join(' ')}
                />
            </span>
        </button>
    );
}
