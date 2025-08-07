// /components/ProductLineItem.tsx
'use client';

import Image from 'next/image';
import { ProductLineItem } from '@/types/cart';
import { MinusIcon, PlusIcon, Trash2Icon } from 'lucide-react';

interface Props {
    item: ProductLineItem;
    onQuantityChange?: (quantity: number) => void;
    onDelete?: () => void;
}

export default function ProductLineItemCard({ item, onQuantityChange, onDelete }: Props) {
    const {
        productId,
        name,
        thumbnailUrl,
        price,
        quantity,
        productOptionId,
        optionName,
        timeSalePrice,
        isTimeSale,
        disableQuantityControls = false,
        showDeleteButton = false,
    } = item;

    const finalPrice = isTimeSale && timeSalePrice ? timeSalePrice : price;
    const totalPrice = finalPrice * quantity;

    const handleIncrease = () => {
        if (!disableQuantityControls && onQuantityChange) onQuantityChange(quantity + 1);
    };

    const handleDecrease = () => {
        if (!disableQuantityControls && quantity > 1 && onQuantityChange) onQuantityChange(quantity - 1);
    };

    return (
        <div className="flex w-full p-4 gap-4 border border-border rounded-lg bg-surface dark:bg-dark-surface dark:border-dark-border">
            <div className="relative w-24 h-24 flex-shrink-0 bg-background dark:bg-dark-background rounded overflow-hidden">
                {thumbnailUrl ? (
                    <Image src={thumbnailUrl} alt={name} fill className="object-cover" />
                ) : (
                    <div className="w-full h-full flex items-center justify-center text-sm text-text-secondary dark:text-dark-text-secondary">이미지 없음</div>
                )}
            </div>
            <div className="flex flex-col flex-1 justify-between">
                <div className="flex flex-col">
                    <span className="text-sm font-semibold text-text-primary dark:text-dark-text-primary">{name}</span>
                    {optionName && (
                        <span className="text-sm text-text-secondary dark:text-dark-text-secondary mt-1">{optionName}</span>
                    )}
                </div>
                <div className="flex justify-between items-center mt-2">
                    <div className="flex items-center gap-2">
                        <button onClick={handleDecrease} disabled={disableQuantityControls} className="px-2 py-1 border rounded border-border text-sm disabled:opacity-50">
                            <MinusIcon size={16} />
                        </button>
                        <span className="min-w-[24px] text-center text-sm">{quantity}</span>
                        <button onClick={handleIncrease} disabled={disableQuantityControls} className="px-2 py-1 border rounded border-border text-sm disabled:opacity-50">
                            <PlusIcon size={16} />
                        </button>
                    </div>
                    <div className="flex items-center gap-2">
                        <span className="text-sm font-bold text-text-primary dark:text-dark-text-primary">{totalPrice.toLocaleString()}원</span>
                        {showDeleteButton && (
                            <button onClick={onDelete} className="text-error">
                                <Trash2Icon size={18} />
                            </button>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
