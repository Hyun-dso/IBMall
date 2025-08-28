// /app/payments/[role]/cart/page.tsx
'use client';
import { useEffect, useMemo, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
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
import { ProductLineItem } from '@/types/cart';

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

    const searchParams = useSearchParams();
    const productId = searchParams.get('productId');
    const [loading, setLoading] = useState(false);


    const [product, setProduct] = useState<ProductLineItem | null>(null);
    const [quantity, setQuantity] = useState(1);
    // const [loading, setLoading] = useState(false);

    // const total = useMemo(
    //     () => items.reduce((acc, cur) => acc + cur.price * cur.quantity, 0),
    //     [items]
    // );

    // 상품 조회
    useEffect(() => {
        if (!productId) return;
        (async () => {
            try {
                setLoading(true);
                const res = await fetch(
                    `/api/products/${productId}`,
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

    const unitPrice = useMemo(() => {
        if (!product) return 0;
        // 필요 시 타임세일 표시가를 결제금액에 반영하려면 아래 주석을 사용
        // return product.isTimeSale && product.timeSalePrice != null ? product.timeSalePrice : product.price;
        return product.price;
    }, [product]);

    const amount = useMemo(() => unitPrice * quantity, [unitPrice, quantity]);

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

        if (!productId || !product) {
            showToast.error('상품 정보가 없습니다.', { group: 'payment' });
            return;
        }

        if (amount <= 0) {
            showToast.error('유효하지 않은 결제 금액입니다.');
            setSubmitting(false);
            return;
        }

        const storeId = process.env.NEXT_PUBLIC_PORTONE_V2_STORE_ID;
        const channelKey = process.env.NEXT_PUBLIC_PORTONE_V2_CHANNEL_KEY;
        if (!storeId || !channelKey) {
            showToast.error('결제 설정이 누락되었습니다.');
            setSubmitting(false);
            return;
        }

        try {
            if (submitting) return;
            setSubmitting(true);
            showToast.loading(guestMode ? '비회원 결제 진행 중…' : '회원 결제 진행 중…', { group });


            const orderName = quantity > 1 ? `${product.name} x${quantity}` : product.name;

            const payment = await PortOne.requestPayment({
                storeId,
                channelKey,
                paymentId: genPaymentId(),
                orderName,
                totalAmount: amount,
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
            if (!tx) { showToast.error('결제 식별자가 비어 있습니다(paymentId).', { group }); return; }

            // 공통 DTO
            // const common: PaymentCommonDto = {

            //     // 멱등/식별
            //     orderUid: tx,
            //     paymentId: tx,
            //     transactionId: tx,

            //     // 금액(서버가 최종 검증)
            //     paidAmount: amount,
            //     orderPrice: amount,

            //     // 상품
            //     productId: Number(productId),
            //     productOptionId: product.productOptionId ?? null,
            //     productName: product.name,
            //     quantity,

            //     // 구매자/수령자
            //     buyerName: form.buyerName.trim(),
            //     buyerEmail: form.buyerEmail.trim(),
            //     buyerPhone: form.buyerPhone.trim(),
            //     recipientName: (form.sendToOther ? form.recipientName : form.buyerName).trim(),
            //     recipientPhone: (form.sendToOther ? form.recipientPhone : form.buyerPhone).trim(),
            //     recipientAddress: `${form.address1} ${form.address2}`.trim(),

            //     // 참고값(서버에서 PG 조회로 덮어씀)
            //     paymentMethod: 'CARD',
            //     status: 'PAID',
            //     pgProvider: 'INICIS',

            // };

            const payload = {
                // ...common,


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
                buyerName: input.name.trim(),
                buyerEmail: input.email.trim() || null,
                buyerPhone: normalizePhone(input.phone),
                recipientName: (input.sendToOther ? input.recipientName : input.email).trim(),
                recipientPhone: (input.sendToOther ? input.recipientPhone : normalizePhone(input.phone)).trim(),
                recipientAddress: `${input.address1} ${input.address2}`.trim(),

                // 참고값(서버에서 PG 조회로 덮어씀)
                paymentMethod: 'CARD',
                status: 'PAID',
                pgProvider: 'INICIS',
                recipientAddress1: input.address1,
                recipientAddress2: input.address2,
            };
            if (guestMode) {

                const res = await fetch('/api/payments/guest/single', {
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
                const res = await fetch('/api/payments/member/single', {
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
