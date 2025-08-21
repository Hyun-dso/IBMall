// /components/cart/CartPageClient.tsx
'use client';

import { useEffect, useMemo, useState } from 'react';
import { useRouter } from 'next/navigation';
import { toast } from 'react-hot-toast';
import type { CartItem } from '@/types/cart';
import { useCartStore } from '@/stores/useCartStore';
import ProductLineItemCard from '@/components/ProductLineItemCard';
import ProductBuyButtons from '@/components/product/ProductBuyButtons'; // 추가

interface Props {
    ssrItems?: CartItem[]; // 회원일 때 서버에서 주입
}

export default function CartPageClient({ ssrItems }: Props) {
    const router = useRouter();
    const updateQty = useCartStore((s) => s.updateQty);
    const remove = useCartStore((s) => s.remove);
    const storeItems = useCartStore((s) => s.items);
    const hydrateFromSSR = useCartStore((s) => s.hydrateFromSSR);
    const [ready, setReady] = useState(false);

    useEffect(() => {
        try {
            if (ssrItems && ssrItems.length) {
                hydrateFromSSR?.(ssrItems);
            }
        } finally {
            setReady(true);
        }
    }, [ssrItems, hydrateFromSSR]);

    const items = ssrItems && ssrItems.length ? ssrItems : storeItems;

    const total = useMemo(
        () =>
            items.reduce((acc, cur) => {
                const unit = cur.isTimeSale && cur.timeSalePrice ? cur.timeSalePrice : cur.price;
                return acc + unit * cur.quantity;
            }, 0),
        [items]
    );

    const handleCheckout = async () => {
        try {
            toast.loading('결제 준비 중');
            // TODO: 비회원 결제 /api/payments/guest/cart, PortOne V2 연동
            toast.dismiss();
            toast.success('결제 모듈 연결 대기중');
        } catch {
            toast.dismiss();
            toast.error('결제 처리 중 오류가 발생했습니다. 잠시 후 다시 시도하세요.');
        }
    };

    if (!ready) {
        return (
            <div className="rounded-xl border border-border dark:border-dark-border bg-surface dark:bg-dark-surface p-6">
                <div className="h-6 w-40 bg-border/50 dark:bg-dark-border/50 animate-pulse mb-4" />
                <div className="space-y-3">
                    <div className="h-24 bg-border/30 dark:bg-dark-border/30 animate-pulse rounded-lg" />
                    <div className="h-24 bg-border/30 dark:bg-dark-border/30 animate-pulse rounded-lg" />
                </div>
            </div>
        );
    }

    if (!items.length) {
        return (
            <div className="rounded-xl border border-border dark:border-dark-border bg-surface dark:bg-dark-surface p-10 text-center">
                <p className="text-text-secondary dark:text-dark-text-secondary">장바구니가 비어 있습니다.</p>
            </div>
        );
    }

    return (
        <div className="grid grid-cols-1 lg:grid-cols-[1fr_360px] gap-6">
            <div className="space-y-3">
                {items.map((it) => (
                    <ProductLineItemCard key={`${it.productId}`}
                        item={{ ...it, disableQuantityControls: false, showDeleteButton: true }}
                        onQuantityChange={(q) => updateQty(it.productId, q)}
                        onDelete={() => remove(it.productId)} />
                ))}
            </div>

            <aside className="h-max rounded-xl border border-border dark:border-dark-border bg-surface dark:bg-dark-surface p-6">
                <div className="flex items-center justify-between mb-4">
                    <span className="text-text-secondary dark:text-dark-text-secondary">총 결제금액</span>
                    <span className="text-xl font-semibold">{total.toLocaleString()}원</span>
                </div>

                {/* 변경: 기본 버튼 → ProductBuyButtons */}
                <ProductBuyButtons
                    mode="cart"
                    className="w-full"
                    label="결제하기"
                    onBuyNow={handleCheckout}
                />
            </aside>
        </div>
    );
}
