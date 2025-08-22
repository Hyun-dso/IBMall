// /app/payments/[role]/cart/page.tsx
'use client';

import { useMemo, useRef, useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useCartStore } from '@/stores/useCartStore';
import CartPanel from '@/components/cart/CartPanel';
import Button from '@/components/ui/Button';
import PortOne, { isPortOneError } from '@portone/browser-sdk/v2';
import { showToast } from '@/lib/toast';
import { validate } from '@/lib/validators/engine';
import type { PaymentRole } from '@/types/payment';
import type { GuestPaymentForm } from '@/types/forms';

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

export default function CartPaymentPage({ params }: any) {
    const role = (params as { role: PaymentRole }).role;
    const router = useRouter();

    const items = useCartStore((s) => s.items);
    const updateQty = useCartStore((s) => s.updateQty);
    const remove = useCartStore((s) => s.remove);
    const clear = useCartStore((s) => s.clear);

    const [submitting, setSubmitting] = useState(false);

    // 게스트용 폼
    const [guestForm, setGuestForm] = useState<GuestPaymentForm>({
        buyerName: '',
        buyerEmail: '',
        buyerPhone: '',
        address1: '',
        address2: '',
        sendToOther: false,
        recipientName: '',
        recipientPhone: '',
    });

    // 회원용 배송지 폼(구매자는 서버 프로필 우선)
    const [memberForm, setMemberForm] = useState({
        recipientName: '',
        recipientPhone: '',
        address1: '',
        address2: '',
    });

    // 다음 우편번호
    const [postcodeLoaded, setPostcodeLoaded] = useState(false);
    const [popupOpened, setPopupOpened] = useState(false);
    const detailRef = useRef<HTMLInputElement | null>(null);

    useEffect(() => {
        // no-op
    }, []);

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

    const openPostcodePopup = async (mode: 'guest' | 'member') => {
        if (popupOpened) return;
        await ensurePostcodeScript();
        setPopupOpened(true);
        new (window as any).daum.Postcode({
            oncomplete: (data: any) => {
                if (mode === 'guest') {
                    setGuestForm((prev) => ({ ...prev, address1: String(data.address || '') }));
                } else {
                    setMemberForm((prev) => ({ ...prev, address1: String(data.address || '') }));
                }
                setPopupOpened(false);
                setTimeout(() => detailRef.current?.focus(), 0);
            },
            onclose: () => setPopupOpened(false),
        }).open();
    };

    const total = useMemo(
        () =>
            items.reduce((acc, cur) => {
                const unit = cur.isTimeSale && cur.timeSalePrice != null ? cur.timeSalePrice : cur.price;
                return acc + unit * cur.quantity;
            }, 0),
        [items]
    );

    const orderName = useMemo(() => {
        if (!items.length) return '';
        const first = items[0]!.name;
        const rest = items.length - 1;
        return rest > 0 ? `${first} 외 ${rest}개` : first;
    }, [items]);

    const handleCheckout = async () => {
        if (!items.length) {
            showToast.error('장바구니가 비어 있습니다.');
            return;
        }

        const storeId = process.env.NEXT_PUBLIC_PORTONE_V2_STORE_ID;
        const channelKey = process.env.NEXT_PUBLIC_PORTONE_V2_CHANNEL_KEY;
        if (!storeId || !channelKey) {
            showToast.error('결제 설정이 누락되었습니다.');
            return;
        }

        // 폼 검증
        if (role === 'guest') {
            const { ok, errors } = validate('guestPayment', guestForm);
            if (!ok) {
                const first = Object.values(errors)[0];
                if (first) showToast.error(first);
                return;
            }
        } else {
            const { recipientName, recipientPhone, address1, address2 } = memberForm;
            if (!recipientName || !recipientPhone || !address1 || !address2) {
                showToast.error('배송지 정보를 모두 입력해 주세요.');
                return;
            }
        }

        if (total <= 0) {
            showToast.error('유효하지 않은 결제 금액입니다.');
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
                totalAmount: total,
                currency: 'CURRENCY_KRW',
                payMethod: 'CARD',
                customer:
                    role === 'guest'
                        ? {
                            fullName: guestForm.buyerName.trim(),
                            email: guestForm.buyerEmail.trim(),
                            phoneNumber: guestForm.buyerPhone.trim(),
                        }
                        : undefined,
            });

            if (!payment) {
                showToast.error('결제가 취소되었습니다.');
                return;
            }
            if (isPortOneError(payment)) {
                showToast.error(payment.pgMessage || payment.message || '결제 실패');
                return;
            }

            const tx = String(payment.paymentId || '').trim();
            if (!tx) {
                showToast.error('결제 식별자가 비어 있습니다(paymentId).');
                return;
            }

            // 공통 payload(undefined 금지)
            const common = {
                orderUid: tx,
                transactionId: tx,
                paidAmount: Number(total),
                orderPrice: Number(total),
                paymentId: payment.paymentId,
                paymentMethod: 'CARD',
                status: 'PAID',
                pgProvider: 'INICIS',
            };

            // 카트 라인아이템(서버에서 단가/합계 재계산)
            const lineItems = items.map((i) => ({
                productId: Number(i.productId),
                productOptionId: i.productOptionId ?? null,
                productName: i.name,
                quantity: Number(i.quantity),
            }));

            if (role === 'guest') {
                const payload = {
                    ...common,
                    productName: orderName,
                    buyerName: guestForm.buyerName.trim(),
                    buyerEmail: guestForm.buyerEmail.trim() || null,
                    buyerPhone: guestForm.buyerPhone.trim(),
                    recipientName: (guestForm.sendToOther ? guestForm.recipientName : guestForm.buyerName).trim(),
                    recipientPhone: (guestForm.sendToOther ? guestForm.recipientPhone : guestForm.buyerPhone).trim(),
                    recipientAddress: `${guestForm.address1} ${guestForm.address2}`.trim(),
                    items: lineItems,
                };

                const res = await fetch('/api/payments/guest/cart', {
                    method: 'POST',
                    credentials: 'include',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload),
                });
                if (!res.ok) {
                    let msg = '결제 검증 실패';
                    try {
                        const j = await res.json();
                        msg = j.message || msg;
                    } catch { }
                    showToast.error(msg);
                    return;
                }
            } else {
                const payload = {
                    ...common,
                    originalAmount: Number(total),
                    recipientName: memberForm.recipientName.trim(),
                    recipientPhone: memberForm.recipientPhone.trim(),
                    recipientAddress: `${memberForm.address1} ${memberForm.address2}`.trim(),
                    items: lineItems.map((x) => ({
                        productId: x.productId,
                        productOptionId: x.productOptionId, // 옵션 상품이면 필수
                        quantity: x.quantity,
                    })),
                };

                const res = await fetch('/api/payments/member/cart', {
                    method: 'POST',
                    credentials: 'include',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload),
                });
                if (!res.ok) {
                    let msg = '결제 검증 실패';
                    try {
                        const j = await res.json();
                        msg = j.message || msg;
                    } catch { }
                    showToast.error(msg);
                    return;
                }
            }

            showToast.success('결제가 완료되었습니다.', { persist: true, showNow: false });
            clear();
            router.push(`/orders/complete?paymentId=${tx}`);
        } catch {
            showToast.error('결제 요청 중 오류가 발생했습니다.');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <main className="min-h-screen px-4 py-8 bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
            <div className="mx-auto w-full max-w-3xl space-y-6">
                {/* 장바구니 섹션: 리스트만 카드로 감싸서 렌더링 */}
                <section className={`p-6 ${CARD_CLASS}`}>
                    <h2 className="text-lg font-semibold mb-4">장바구니</h2>
                    {items.length === 0 ? (
                        <div className="text-text-secondary dark:text-dark-text-secondary">담긴 상품이 없습니다.</div>
                    ) : (
                        <CartPanel
                            items={items}
                            onQtyChange={(pid, qty, optId) => updateQty(pid, qty, optId ?? null)}
                            onRemove={(pid, optId) => remove(pid, optId ?? null)}
                            className="space-y-2"
                        />
                    )}
                </section>

                {/* 주문/배송 및 결제 섹션 */}
                <section className={`p-6 ${CARD_CLASS}`}>
                    <h2 className="text-lg font-semibold mb-4">주문/배송 정보</h2>

                    {role === 'guest' ? (
                        <>
                            <h3 className="text-base font-semibold mb-2">구매자 정보</h3>
                            <div className="mb-6 border border-border dark:border-dark-border rounded-md overflow-hidden bg-surface dark:bg-dark-surface">
                                <input
                                    name="buyerName"
                                    placeholder="이름"
                                    value={guestForm.buyerName}
                                    onChange={(e) => setGuestForm((p) => ({ ...p, buyerName: e.target.value }))}
                                    required
                                    className={`${INPUT_CLASS} border-b border-border dark:border-dark-border`}
                                />
                                <input
                                    name="buyerEmail"
                                    type="email"
                                    placeholder="이메일"
                                    value={guestForm.buyerEmail}
                                    onChange={(e) => setGuestForm((p) => ({ ...p, buyerEmail: e.target.value }))}
                                    required
                                    className={`${INPUT_CLASS} border-b border-border dark:border-dark-border`}
                                />
                                <input
                                    name="buyerPhone"
                                    placeholder="전화번호"
                                    value={guestForm.buyerPhone}
                                    onChange={(e) => setGuestForm((p) => ({ ...p, buyerPhone: e.target.value }))}
                                    required
                                    className={INPUT_CLASS}
                                />
                            </div>

                            <div className="flex items-center justify-between mb-2">
                                <h3 className="text-base font-semibold">배송지</h3>
                                <Switch
                                    checked={guestForm.sendToOther}
                                    onChange={(v) => setGuestForm((p) => ({ ...p, sendToOther: v }))}
                                    label="다른 사람에게 보내기"
                                />
                            </div>

                            <div className="mb-6 border border-border dark:border-dark-border rounded-md overflow-hidden bg-surface dark:bg-dark-surface">
                                <input
                                    name="address1"
                                    placeholder="주소"
                                    value={guestForm.address1}
                                    readOnly
                                    onClick={() => openPostcodePopup('guest')}
                                    onFocus={() => openPostcodePopup('guest')}
                                    required
                                    className={`${INPUT_CLASS} border-b border-border dark:border-dark-border cursor-pointer`}
                                />
                                <input
                                    name="address2"
                                    placeholder="상세주소"
                                    value={guestForm.address2}
                                    ref={detailRef}
                                    onChange={(e) => setGuestForm((p) => ({ ...p, address2: e.target.value }))}
                                    required
                                    className={`${INPUT_CLASS} ${guestForm.sendToOther ? 'border-b border-border dark:border-dark-border' : ''}`}
                                />
                                {guestForm.sendToOther && (
                                    <>
                                        <input
                                            name="recipientName"
                                            placeholder="수령인 이름"
                                            value={guestForm.recipientName ?? ''}
                                            onChange={(e) => setGuestForm((p) => ({ ...p, recipientName: e.target.value }))}
                                            required
                                            className={`${INPUT_CLASS} border-b border-border dark:border-dark-border`}
                                        />
                                        <input
                                            name="recipientPhone"
                                            placeholder="수령인 연락처"
                                            value={guestForm.recipientPhone ?? ''}
                                            onChange={(e) => setGuestForm((p) => ({ ...p, recipientPhone: e.target.value }))}
                                            required
                                            className={INPUT_CLASS}
                                        />
                                    </>
                                )}
                            </div>

                            <div className="mb-4 flex items-center justify-between">
                                <span className="text-text-secondary dark:text-dark-text-secondary">결제 금액</span>
                                <span className="text-xl font-semibold">{currency.format(total)}원</span>
                            </div>

                            <Button className="w-full py-3 font-bold" disabled={submitting || !items.length} onClick={handleCheckout}>
                                {submitting ? '처리 중...' : '결제 진행'}
                            </Button>
                        </>
                    ) : (
                        <>
                            <h3 className="text-base font-semibold mb-2">배송지</h3>
                            <div className="mb-6 border border-border dark:border-dark-border rounded-md overflow-hidden bg-surface dark:bg-dark-surface">
                                <input
                                    name="address1"
                                    placeholder="주소"
                                    value={memberForm.address1}
                                    readOnly
                                    onClick={() => openPostcodePopup('member')}
                                    onFocus={() => openPostcodePopup('member')}
                                    required
                                    className={`${INPUT_CLASS} border-b border-border dark:border-dark-border cursor-pointer`}
                                />
                                <input
                                    name="address2"
                                    placeholder="상세주소"
                                    value={memberForm.address2}
                                    ref={detailRef}
                                    onChange={(e) => setMemberForm((p) => ({ ...p, address2: e.target.value }))}
                                    required
                                    className={`${INPUT_CLASS} border-b border-border dark:border-dark-border`}
                                />
                                <input
                                    name="recipientName"
                                    placeholder="수령인 이름"
                                    value={memberForm.recipientName}
                                    onChange={(e) => setMemberForm((p) => ({ ...p, recipientName: e.target.value }))}
                                    required
                                    className={`${INPUT_CLASS} border-b border-border dark:border-dark-border`}
                                />
                                <input
                                    name="recipientPhone"
                                    placeholder="수령인 연락처"
                                    value={memberForm.recipientPhone}
                                    onChange={(e) => setMemberForm((p) => ({ ...p, recipientPhone: e.target.value }))}
                                    required
                                    className={INPUT_CLASS}
                                />
                            </div>

                            <div className="mb-4 flex items-center justify-between">
                                <span className="text-text-secondary dark:text-dark-text-secondary">결제 금액</span>
                                <span className="text-xl font-semibold">{currency.format(total)}원</span>
                            </div>

                            <Button className="w-full py-3 font-bold" disabled={submitting || !items.length} onClick={handleCheckout}>
                                {submitting ? '처리 중...' : '결제 진행'}
                            </Button>
                        </>
                    )}
                </section>
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
