// /components/payments/SinglePaymentPage.tsx
'use client';

import { useEffect, useMemo, useRef, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import ProductLineItemCard from '@/components/ProductLineItemCard';
import Button from '@/components/ui/Button';
import PortOne, { isPortOneError } from '@portone/browser-sdk/v2';
import { showToast } from '@/lib/toast';
import { validate } from '@/lib/validators/engine';
import type { PaymentRole, OrderIntentPayload } from '@/types/payment';
import type { ProductLineItem } from '@/types/cart';
import type { GuestPaymentForm } from '@/types/forms';
import { verifyOrderIntent, completeGuestSinglePayment } from '@/lib/payments/client';

const CARD_CLASS =
    'border border-border dark:border-dark-border rounded-lg bg-surface dark:bg-dark-surface shadow-sm';
const INPUT_CLASS =
    'w-full px-4 py-3 bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none';

const currency = new Intl.NumberFormat('ko-KR');

function genPaymentId() {
    const parts = [...crypto.getRandomValues(new Uint32Array(2))].map((w) =>
        w.toString(16).padStart(8, '0'),
    );
    return parts.join('');
}

type Props = { role: PaymentRole };

export default function SinglePaymentPage({ role }: Props) {
    const router = useRouter();
    const searchParams = useSearchParams();
    const intentId = searchParams.get('intent');

    const [intent, setIntent] = useState<OrderIntentPayload | null>(null);
    const [product, setProduct] = useState<ProductLineItem | null>(null);
    const [orderName, setOrderName] = useState<string>('');
    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);
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

    const detailRef = useRef<HTMLInputElement>(null);
    const [postcodeLoaded, setPostcodeLoaded] = useState(false);
    const [popupOpened, setPopupOpened] = useState(false);

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

    useEffect(() => {
        if (!intentId) {
            setErrorMsg('잘못된 접근입니다. 의도 토큰이 없습니다.');
            return;
        }
        (async () => {
            try {
                setLoading(true);
                const { intent: iv, orderName: oname, product: p } = await verifyOrderIntent(intentId, role);
                const unit = iv.unitPrice;
                const qty = iv.quantity;
                const item: ProductLineItem = {
                    productId: p.productId,
                    name: p.name,
                    price: unit,
                    timeSalePrice: null,
                    isTimeSale: false,
                    thumbnailUrl: p.thumbnailUrl ?? null,
                    quantity: qty,
                    paidAmount: unit * qty,
                    optionName: p.optionName ?? null,
                    productOptionId: iv.optionId ?? null,
                    disableQuantityControls: true,
                    showDeleteButton: false,
                };
                setIntent(iv);
                setOrderName(oname);
                setProduct(item);
            } catch (e: any) {
                setErrorMsg(e?.message || '의도 검증 실패');
            } finally {
                setLoading(false);
            }
        })();
    }, [intentId, role]);

    const setField = <K extends keyof GuestPaymentForm>(k: K, v: GuestPaymentForm[K]) =>
        setForm((prev) => ({ ...prev, [k]: v }));

    const amount = useMemo(() => {
        if (!product) return 0;
        return product.price * product.quantity;
    }, [product]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!intent || !product) return;

        const check = validate('guestPayment', form);
        if (!check.ok) {
            const first = Object.values(check.errors)[0];
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

            // 게스트 단일 결제 완료 콜 → 서버는 intent 기준으로 정산해야 함
            const ok = await completeGuestSinglePayment({
                intentId: intent.intentId,
                paymentId: payment.paymentId,
                orderUid: payment.paymentId,
                // 레거시 호환 필드(서버는 신뢰하지 말고 무시 가능)
                productId: product.productId,
                productOptionId: product.productOptionId,
                quantity: product.quantity,
                orderPrice: product.price,
                paidAmount: amount,
                buyerName: form.buyerName,
                buyerEmail: form.buyerEmail,
                buyerPhone: form.buyerPhone,
                recipientName: form.sendToOther ? form.recipientName : form.buyerName,
                recipientPhone: form.sendToOther ? form.recipientPhone : form.buyerPhone,
                recipientAddress: `${form.address1} ${form.address2}`.trim(),
            });

            if (!ok) {
                showToast.error('결제 검증 실패', { group: 'payment' });
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
                        <ProductLineItemCard item={product} onQuantityChange={() => { }} />
                    ) : (
                        <div className="text-error">상품을 찾을 수 없습니다.</div>
                    )}
                </div>

                <form noValidate onSubmit={handleSubmit} className={`w-full max-w-md p-6 ${CARD_CLASS}`}>
                    <h1 className="text-2xl font-bold mb-6 text-center">결제 정보</h1>

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

                    <AddressBlock
                        form={form}
                        setField={setField}
                        openPostcodePopup={openPostcodePopup}
                        detailRef={detailRef}
                    />

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

function AddressBlock({
    form,
    setField,
    openPostcodePopup,
    detailRef,
}: {
    form: GuestPaymentForm;
    setField: <K extends keyof GuestPaymentForm>(k: K, v: GuestPaymentForm[K]) => void;
    openPostcodePopup: () => Promise<void>;
    detailRef: React.RefObject<HTMLInputElement | null>;
}) {
    return (
        <>
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
        </>
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
