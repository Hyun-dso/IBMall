// /components/cart/CartPanel.tsx
'use client';

import ProductLineItemCard from '@/components/ProductLineItemCard';
import type { CartItem } from '@/types/cart';

type Props = {
    items: CartItem[];
    onQtyChange: (productId: number, qty: number, productOptionId?: number | null) => void;
    onRemove: (productId: number, productOptionId?: number | null) => void;
    className?: string; // 부모가 padding/overflow 등 컨테이너 스타일을 제어
};

export default function CartPanel({ items, onQtyChange, onRemove, className }: Props) {
    // 리스트만 렌더링. 빈 경우 부모가 대체 UI를 책임짐.
    if (!items || items.length === 0) return null;

    return (
        <ul className={['space-y-2', className || ''].join(' ')}>
            {items.map((it) => (
                <li key={`cart-${it.productId}-${it.productOptionId ?? 'none'}`}>
                    <ProductLineItemCard
                        item={{ ...it, disableQuantityControls: false, showDeleteButton: true }}
                        onQuantityChange={(q) => onQtyChange(it.productId, q, it.productOptionId ?? null)}
                        onDelete={() => onRemove(it.productId, it.productOptionId ?? null)}
                    />
                </li>
            ))}
        </ul>
    );
}
