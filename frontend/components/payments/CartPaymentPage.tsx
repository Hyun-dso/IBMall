// /components/payments/CartPaymentPage.tsx
'use client';

import { useMemo, useState } from 'react';
import { useRouter } from 'next/navigation';
import Button from '@/components/ui/Button';
import ProductLineItemCard from '@/components/ProductLineItemCard';
import { showToast } from '@/lib/toast';
import { useCartStore } from '@/stores/useCartStore';
import type { CartItem } from '@/types/cart';
import type { PaymentRole } from '@/types/payment';
import { createOrderIntentForCart } from '@/lib/payments/client';

const CARD_CLASS =
    'border border-border dark:border-dark-border rounded-lg bg-surface dark:bg-dark-surface shadow-sm';

const currency = new Intl.NumberFormat('ko-KR');

type Props = {
    role: PaymentRole;
    items?: CartItem[]; // SSR 주입(회원). 비회원은 생략하고 zustand 사용.
};

export default function CartPaymentPage({ role, items }: Props) {
    const router = useRouter();
    const storeItems = useCartStore((s) => s.items);
    const [pending, setPending] = useState(false);

    const effectiveItems = items ?? storeItems;

    const total = useMemo(
        () =>
            effectiveItems.reduce((acc, cur) => {
                const unit =
                    cur.isTimeSale && cur.timeSalePrice != null ? cur.timeSalePrice : cur.price;
                return acc + unit * cur.quantity;
            }, 0),
        [effectiveItems],
    );

    const handleCheckout = async () => {
        if (!effectiveItems.length) {
            showToast.error('장바구니가 비어 있습니다.');
            return;
        }
        if (pending) return;
        try {
            setPending(true);
            // 장바구니 전체에 대한 주문 의도 생성(서버에서 가격/재고/판매상태 검증 필수)
            const { intentId } = await createOrderIntentForCart({
                role,
                // 서버는 클라이언트 값을 그대로 신뢰하지 말고, 식별자 기반으로 재조회/검증해야 함
                items: effectiveItems.map((i) => ({
                    productId: i.productId,
                    productOptionId: i.productOptionId ?? null,
                    quantity: i.quantity,
                })),
            });
            // 장바구니 결제 플로우는 cart 경로 유지. intent 기반으로 다음 단계 진입.
            router.push(`/payments/${role}/single?intent=${encodeURIComponent(intentId)}`);
            // 주: cart 결제를 single 결제 화면으로 통합 정산할지, 별도 cart 결제 화면으로 둘지 정책에 맞게 수정 가능
        } catch (e: any) {
            showToast.error(e?.message || '주문 생성에 실패했습니다.');
        } finally {
            setPending(false);
        }
    };

    return (
        <main className="min-h-screen px-4 py-8 bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
            <div className="mx-auto w-full max-w-3xl space-y-6">
                <section className={`p-4 ${CARD_CLASS}`}>
                    <h2 className="text-lg font-semibold mb-3">장바구니</h2>
                    {effectiveItems.length === 0 ? (
                        <div className="text-text-secondary dark:text-dark-text-secondary">
                            담긴 상품이 없습니다.
                        </div>
                    ) : (
                        <ul className="space-y-3">
                            {effectiveItems.map((it) => (
                                <li key={`${it.productId}-${it.productOptionId ?? 'none'}`}>
                                    <ProductLineItemCard item={it} />
                                </li>
                            ))}
                        </ul>
                    )}
                </section>

                <section className={`p-4 ${CARD_CLASS}`}>
                    <div className="flex items-center justify-between">
                        <span className="text-text-secondary dark:text-dark-text-secondary">합계</span>
                        <span className="text-xl font-semibold">{currency.format(total)}원</span>
                    </div>
                    <Button
                        className="w-full mt-4"
                        disabled={pending || effectiveItems.length === 0}
                        onClick={handleCheckout}
                    >
                        {pending ? '처리 중...' : '결제 진행'}
                    </Button>
                </section>
            </div>
        </main>
    );
}
