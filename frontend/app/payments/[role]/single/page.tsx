// /app/payments/guest/single/page.tsx
'use client';

import { useSearchParams, useRouter } from 'next/navigation';
import { useEffect, useMemo, useRef, useState } from 'react';
import ProductLineItemCard from '@/components/ProductLineItemCard';
import Button from '@/components/ui/Button';
import PortOne, { isPortOneError } from '@portone/browser-sdk/v2';
import { validate } from '@/lib/validators/engine';
import { showToast } from '@/lib/toast';

import type { ProductLineItem } from '@/types/cart';
import type { GuestPaymentForm } from '@/types/forms';

const CARD_CLASS =
    'border border-border dark:border-dark-border rounded-lg bg-surface dark:bg-dark-surface shadow-sm';
const INPUT_CLASS =
    'w-full px-4 py-3 bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none';

const currency = new Intl.NumberFormat('ko-KR');

function normalizeDigits(p: string) {
    return String(p || '').replace(/[^0-9]/g, '');
}

function genPaymentId() {
    const parts = [...crypto.getRandomValues(new Uint32Array(2))].map((w) =>
        w.toString(16).padStart(8, '0'),
    );
    return parts.join('');
}

export default function GuestSinglePaymentPage() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const productId = searchParams.get('productId');

    const [product, setProduct] = useState<ProductLineItem | null>(null);
    const [quantity, setQuantity] = useState(1);
    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);

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

    // 다음 우편번호 팝업
    const [postcodeLoaded, setPostcodeLoaded] = useState(false);
    const [popupOpened, setPopupOpened] = useState(false);
    const detailRef = useRef<HTMLInputElement>(null);

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
                setForm((prev) => ({ ...prev, address1: String(data.address || '') }));
                setPopupOpened(false);
                setTimeout(() => detailRef.current?.focus(), 0);
            },
            onclose: () => setPopupOpened(false),
        }).open();
    };

    // 상품 조회
    useEffect(() => {
        if (!productId) return;
        (async () => {
            try {
                setLoading(true);
                const res = await fetch(
                    `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/products/${productId}`,
                    { credentials: 'include' },
                );
                if (!res.ok) {
                    let msg = '상품 조회 실패';
                    try {
                        const j = await res.json();
                        msg = j?.message || msg;
                    } catch { }
                    showToast.error(msg, { group: 'payment' });
                    return;
                }
                const json = await res.json();
                const data = json.data.product;

                const line: ProductLineItem = {
                    productId: data.productId,
                    name: data.name,
                    price: data.price, // 서버가 최종 검증하므로 여기선 표시/요청값
                    isTimeSale: Boolean(data.isTimeSale),
                    timeSalePrice: typeof data.timeSalePrice === 'number' ? data.timeSalePrice : null,
                    thumbnailUrl: data.thumbnailUrl ?? null,
                    optionName: data.optionName ?? null,
                    quantity: 1,
                    disableQuantityControls: false,
                    showDeleteButton: false,
                    productOptionId: null
                };
                setProduct(line);
            } catch {
                showToast.error('상품 정보를 불러오는 중 오류가 발생했어요', { group: 'payment' });
            } finally {
                setLoading(false);
            }
        })();
    }, [productId]);

    const setField = <K extends keyof GuestPaymentForm>(k: K, v: GuestPaymentForm[K]) =>
        setForm((prev) => ({ ...prev, [k]: v }));

    const unitPrice = useMemo(() => {
        if (!product) return 0;
        // 필요 시 타임세일 표시가를 결제금액에 반영하려면 아래 주석을 사용
        // return product.isTimeSale && product.timeSalePrice != null ? product.timeSalePrice : product.price;
        return product.price;
    }, [product]);

    const amount = useMemo(() => unitPrice * quantity, [unitPrice, quantity]);

    const handleQuantityChange = (q: number) => {
        if (q < 1) return;
        setQuantity(q);
        setProduct((prev) => (prev ? { ...prev, quantity: q } : prev));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!productId || !product) {
            showToast.error('상품 정보가 없습니다.', { group: 'payment' });
            return;
        }
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

            const orderName =
                quantity > 1 ? `${product.name} x${quantity}` : product.name;

            const payment = await PortOne.requestPayment({
                storeId,
                channelKey,
                paymentId: genPaymentId(),
                orderName,
                totalAmount: amount,
                currency: 'CURRENCY_KRW',
                payMethod: 'CARD',
                customer: {
                    fullName: form.buyerName.trim(),
                    email: form.buyerEmail.trim(),
                    phoneNumber: form.buyerPhone.trim(),
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

            const tx = String(payment.paymentId || '').trim();
            if (!tx) {
                showToast.error('결제 식별자가 비어 있습니다(paymentId).', { group: 'payment' });
                return;
            }

            // 서버 검증/주문 저장 — Next API 프록시를 통해 백엔드로 전달
            try {
                const payload = {
                    // 멱등/식별
                    orderUid: tx,
                    paymentId: tx,
                    transactionId: tx,

                    // 금액(서버가 최종 검증)
                    paidAmount: amount,
                    orderPrice: amount,

                    // 상품
                    productId: Number(productId),
                    productOptionId: product.productOptionId ?? null,
                    productName: product.name,
                    quantity,

                    // 구매자/수령자
                    buyerName: form.buyerName.trim(),
                    buyerEmail: form.buyerEmail.trim(),
                    buyerPhone: form.buyerPhone.trim(),
                    recipientName: (form.sendToOther ? form.recipientName : form.buyerName).trim(),
                    recipientPhone: (form.sendToOther ? form.recipientPhone : form.buyerPhone).trim(),
                    recipientAddress: `${form.address1} ${form.address2}`.trim(),

                    // 참고값(서버에서 PG 조회로 덮어씀)
                    paymentMethod: 'CARD',
                    status: 'PAID',
                    pgProvider: 'INICIS',
                };

                const res = await fetch('/api/payments/guest/single', {
                    method: 'POST',
                    credentials: 'include',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload),
                });

                if (!res.ok) {
                    let msg = '결제 검증 실패';
                    try {
                        const j = await res.json();
                        msg = j?.message || msg;
                    } catch { }
                    showToast.error(msg, { group: 'payment' });
                    return;
                }
            } catch {
                showToast.error('결제 검증 요청 실패', { group: 'payment' });
                return;
            }
            try {
                sessionStorage.setItem(
                    'guestOrderHint',
                    JSON.stringify({
                        name: form.buyerName.trim(),
                        phone: normalizeDigits(form.buyerPhone),
                        orderUid: tx, // paymentId == orderUid 로 사용
                    })
                );
            } catch { }
            showToast.success('결제가 완료되었습니다.', { group: 'payment', persist: true, showNow: false });
            router.push(`/orders/complete?paymentId=${encodeURIComponent(tx)}`);
        } catch {
            showToast.error('결제 요청 중 오류가 발생했어요', { group: 'payment' });
        } finally {
            setSubmitting(false);
        }
    };

    if (!productId) {
        return (
            <main className="min-h-screen flex items-center justify-center px-4 py-8 bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
                <div className={`p-6 ${CARD_CLASS}`}>잘못된 접근입니다.</div>
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
                        <ProductLineItemCard item={product} onQuantityChange={handleQuantityChange} />
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

/* 내부 토글 스위치 */
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
