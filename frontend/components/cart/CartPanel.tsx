// /components/cart/CartPanel.tsx
'use client';

import ProductLineItemCard from '@/components/ProductLineItemCard';
import Button from '@/components/ui/Button';
import type { CartItem } from '@/types/cart';

const PANEL_CLASS =
    'rounded-xl border border-border dark:border-dark-border bg-surface dark:bg-dark-surface shadow-lg';
const SECTION_BORDER = 'border-border/60 dark:border-dark-border/60';

type Props = {
    items: CartItem[];
    total: number;
    onQtyChange: (productId: number, qty: number, productOptionId?: number | null) => void;
    onRemove: (productId: number, productOptionId?: number | null) => void;
    onCheckout: () => void;
    embedded?: boolean; // true면 페이지 내 임베디드 렌더링, false면 플로팅 패널 내부에 쓰기
    headerTitle?: string;
    footerPrimaryText?: string;
    disabled?: boolean;
};

export default function CartPanel({
    items,
    total,
    onQtyChange,
    onRemove,
    onCheckout,
    embedded = false,
    headerTitle = '빠른 장바구니',
    footerPrimaryText = '결제 진행',
    disabled = false,
}: Props) {
    return (
        <div className={embedded ? PANEL_CLASS : `w-[420px] max-h-[70vh] flex flex-col ${PANEL_CLASS}`}>
            <header className={`px-4 py-3 border-b ${SECTION_BORDER}`}>
                <h2 className="text-sm font-medium text-text-secondary dark:text-dark-text-secondary">
                    {headerTitle}
                </h2>
            </header>

            <div className={`flex-1 ${embedded ? '' : 'overflow-y-auto'} px-3 py-3 space-y-2`}>
                {items.length === 0 ? (
                    <div className="text-text-secondary dark:text-dark-text-secondary">담긴 상품이 없습니다.</div>
                ) : (
                    <ul className="space-y-2">
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
                )}
            </div>

            <footer className={`px-4 py-3 border-t ${SECTION_BORDER}`}>
                <div className="flex items-center justify-between mb-2">
                    <span className="text-text-secondary dark:text-dark-text-secondary">합계</span>
                    <span className="font-semibold text-text-primary dark:text-dark-text-primary ">
                        {total.toLocaleString()}원
                    </span>
                </div>
                <div className="flex gap-2">
                    <Button
                        className="flex-1"
                        disabled={disabled || items.length === 0}
                        onClick={onCheckout}
                    >
                        {footerPrimaryText}
                    </Button>
                </div>
            </footer>
        </div>
    );
}
