// /app/payments/[role]/cart/page.tsx
'use client';
import { useMemo, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useCartStore } from '@/stores/useCartStore';
import PortOne, { isPortOneError } from '@portone/browser-sdk/v2';
import { showToast } from '@/lib/toast';
import PaymentsForm from '@/components/form/PaymentsForm';
import {
    GuestCheckoutInput,
    PaymentCommonDto,
    GuestPaymentCreateDto,
    MemberPaymentCreateDto,
    CartLineItemDto,
} from '@/types/payment';
import { normalizePhone } from '@/lib/validators/rules';

const guestMode = true;

function genPaymentId() {
    const parts = [...crypto.getRandomValues(new Uint32Array(2))].map(w =>
        w.toString(16).padStart(8, '0')
    );
    return parts.join('');
}

export default function CartPaymentPage() {
    const router = useRouter();
    const [submitting, setSubmitting] = useState(false);
    const group = 'payment';
    const items = useCartStore(s => s.items);

    const total = useMemo(
        () => items.reduce((acc, cur) => acc + cur.price * cur.quantity, 0),
        [items]
    );

    const orderName = useMemo(() => {
        if (!items.length) return '';
        const first = items[0]!.name;
        const rest = items.length - 1;
        return rest > 0 ? `${first} 외 ${rest}개` : first;
    }, [items]);

    // 폼에서 온 입력값을 받아 결제 + 서버 DTO 전송
    const handleSubmit = async (input: GuestCheckoutInput) => {
        if (submitting) return;
        setSubmitting(true);

        const storeId = process.env.NEXT_PUBLIC_PORTONE_V2_STORE_ID;
        const channelKey = process.env.NEXT_PUBLIC_PORTONE_V2_CHANNEL_KEY;
        if (!storeId || !channelKey) {
            showToast.error('결제 설정이 누락되었습니다.');
            setSubmitting(false);
            return;
        }
        if (total <= 0) {
            showToast.error('유효하지 않은 결제 금액입니다.');
            setSubmitting(false);
            return;
        }

        try {
            showToast.loading(guestMode ? '비회원 결제 진행 중…' : '회원 결제 진행 중…', { group });

            const payment = await PortOne.requestPayment({
                storeId,
                channelKey,
                paymentId: genPaymentId(),
                orderName,
                totalAmount: total,
                currency: 'CURRENCY_KRW',
                payMethod: 'CARD',
                customer: guestMode
                    ? {
                        fullName: input.name,
                        email: input.email,
                        phoneNumber: normalizePhone(input.phone),
                    }
                    : undefined,
            });

            if (!payment) { showToast.error('결제가 취소되었습니다.'); return; }
            if (isPortOneError(payment)) {
                showToast.error(payment.pgMessage || payment.message || '결제 실패'); return;
            }

            const tx = String(payment.paymentId || '').trim();
            if (!tx) { showToast.error('결제 식별자가 비어 있습니다(paymentId).'); return; }

            // 공통 DTO
            const common: PaymentCommonDto = {
                orderUid: tx,
                transactionId: tx,
                paidAmount: Number(total),
                orderPrice: Number(total),
                paymentId: payment.paymentId!,
                paymentMethod: 'CARD',
                status: 'PAID',
                pgProvider: 'INICIS',
                items: items.map<CartLineItemDto>((i) => ({
                    productId: Number(i.productId),
                    quantity: Number(i.quantity),
                    ...(i.productOptionId != null ? { productOptionId: Number(i.productOptionId) } : {}),
                })),
            };

            if (guestMode) {
                const payload = {
                    ...common,
                    productName: orderName,
                    buyerName: input.name.trim(),
                    buyerEmail: input.email.trim() || null,
                    buyerPhone: normalizePhone(input.phone),
                    recipientName: (input.sendToOther ? input.recipientName : input.name)!.trim(),
                    recipientPhone: normalizePhone((input.sendToOther ? input.recipientPhone : input.phone)!),
                    recipientAddress1: input.address1,
                    recipientAddress2: input.address2,
                };

                const res = await fetch('/api/payments/guest/cart', {
                    method: 'POST',
                    credentials: 'include',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload),
                });
                if (!res.ok) {
                    const j = await res.json().catch(() => ({}));
                    showToast.error(j.message || '결제 검증 실패', { group });
                    return;
                }
            } else {
                const payload: MemberPaymentCreateDto = {
                    ...common,
                    productName: orderName,
                    originalAmount: Number(total),
                    recipientName: (input.sendToOther ? input.recipientName : input.name)!.trim(),
                    recipientPhone: normalizePhone((input.sendToOther ? input.recipientPhone : input.phone)!),
                    recipientAddress1: input.address1,
                    recipientAddress2: input.address2,
                };

                const res = await fetch('/api/payments/member/cart', {
                    method: 'POST',
                    credentials: 'include',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload),
                });
                if (!res.ok) {
                    const j = await res.json().catch(() => ({}));
                    showToast.error(j.message || '결제 검증 실패', { group });
                    return;
                }
            }

            showToast.success('결제가 완료되었습니다.', { group, persist: true });
            // useCartStore().clear();  // 필요 시 비우기
            router.push(`/orders/complete?paymentId=${tx}`);
        } catch {
            showToast.error('결제 요청 중 오류가 발생했습니다.', { group });
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <PaymentsForm
            title="결제 정보 입력"
            submitLabel="결제"
            onSubmit={handleSubmit}
        />
    );
}
