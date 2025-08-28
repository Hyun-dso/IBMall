// /components/ProductLineItemCard.tsx
'use client';

import Image from 'next/image';
import type { ProductLineItem } from '@/types/cart';
import { MinusIcon, PlusIcon, Trash2Icon } from 'lucide-react';
import { showToast } from '@/lib/toast';

interface Props {
    item: ProductLineItem;
    onQuantityChange?: (quantity: number) => void;
    onDelete?: () => void;
}

export default function ProductLineItemCard({ item, onQuantityChange, onDelete }: Props) {
    const {
        name,
        thumbnailUrl,
        price,
        quantity,
        optionName,
        timeSalePrice,
        isTimeSale,
        disableQuantityControls = false,
        showDeleteButton = false,
    } = item;

    const unitPrice = isTimeSale && timeSalePrice ? timeSalePrice : price;
    const totalPrice = unitPrice * quantity;

    const handleIncrease = () => {
        if (!disableQuantityControls && onQuantityChange) onQuantityChange(quantity + 1);
        showToast.success(quantity.toString());
    };

    const handleDecrease = () => {
        if (!disableQuantityControls && quantity > 1 && onQuantityChange) onQuantityChange(quantity - 1);
        showToast.success(quantity.toString());
    };

    return (
        <div className="flex w-full p-4 gap-4 border border-[var(--border)] rounded-lg bg-[var(--background)]">
            <div className="relative w-24 h-24 flex-shrink-0 bg-[var(--background)] rounded overflow-hidden">
                {thumbnailUrl ? (
                    <Image src={thumbnailUrl} alt={name} fill className="object-cover" />
                ) : (
                    <div className="w-full h-full flex items-center justify-center text-sm bg-[var(--surface)]">
                        이미지 없음
                    </div>
                )}
            </div>
            <div className="flex flex-col flex-1 justify-between">
                <div className="flex flex-col">
                    <span className="text-sm font-semibold">{name}</span>
                    {optionName && (
                        <span className="text-sm mt-1">{optionName}</span>
                    )}

                    {/* 단가 표시 */}
                    <div className="mt-1 flex items-baseline gap-2">
                        {isTimeSale && timeSalePrice && timeSalePrice < price ? (
                            <>
                                <span className="text-xs line-through text-[var(--foreground-secondary)]">
                                    {price.toLocaleString()}원
                                </span>
                                <span className="text-sm font-semibold">
                                    {timeSalePrice.toLocaleString()}원
                                </span>
                            </>
                        ) : (
                            <span className="text-sm font-semibold">
                                {price.toLocaleString()}원
                            </span>
                        )}
                    </div>
                </div>

                <div className="flex justify-between items-center mt-3">
                    <div className="flex items-center gap-2">
                        <button
                            type="button"
                            onClick={handleDecrease}
                            disabled={disableQuantityControls}
                            aria-label="수량 감소"
                            className="text-[var(--foreground)] px-2 py-1 border rounded border-[var(--border)] text-sm disabled:opacity-50"
                        >
                            <MinusIcon size={16} />
                        </button>
                        <span className="text-[var(--foreground)] min-w-[24px] text-center text-sm">{quantity}</span>
                        <button
                            type="button"
                            onClick={handleIncrease}
                            disabled={disableQuantityControls}
                            aria-label="수량 증가"
                            className="text-[var(--foreground)] px-2 py-1 border rounded border-[var(--border)] text-sm disabled:opacity-50"
                        >
                            <PlusIcon size={16} />
                        </button>
                    </div>

                    <div className="flex items-center gap-2">
                        <div className="flex flex-col items-end">
                            <span className="text-xs text-[var(--foreground-secondary)]">합계</span>
                            <span className="text-sm font-bold text-[var(--foreground)]">
                                {totalPrice.toLocaleString()}원
                            </span>
                        </div>
                        {showDeleteButton && (
                            <button type="button" onClick={onDelete} aria-label="상품 삭제" className="text-error">
                                <Trash2Icon size={18} />
                            </button>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}