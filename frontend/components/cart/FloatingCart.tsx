// /components/cart/FloatingCart.tsx
'use client';

import { useEffect, useMemo, useState } from 'react';
import { createPortal } from 'react-dom';
import { useCartStore } from '@/stores/useCartStore';
import ProductLineItemCard from '@/components/ProductLineItemCard';
import Button from '@/components/ui/Button';

const MIN_DESKTOP_WIDTH = 0;

export default function FloatingCart() {
    const items = useCartStore((s) => s.items);
    const updateQty = useCartStore((s) => s.updateQty);
    const remove = useCartStore((s) => s.remove);

    const [mounted, setMounted] = useState(false);
    const [open, setOpen] = useState(false);
    const [isDesktop, setIsDesktop] = useState(true);

    const total = useMemo(
        () =>
            items.reduce((acc, cur) => {
                const unit = cur.isTimeSale && cur.timeSalePrice ? cur.timeSalePrice : cur.price;
                return acc + unit * cur.quantity;
            }, 0),
        [items]
    );

    useEffect(() => {
        setMounted(true);
        const media = () => setIsDesktop(window.innerWidth >= MIN_DESKTOP_WIDTH);
        media();
        window.addEventListener('resize', media);
        return () => window.removeEventListener('resize', media);
    }, []);

    if (!mounted || !isDesktop) return null;
    if (!items.length) return null;

    const panel = (
        <div className="fixed right-4 bottom-20 z-50">
            <div className="flex flex-col items-end">
                <div className="flex justify-end">
                    <Button
                        size="md"
                        variant="solid"
                        className="shadow-md"
                        onClick={() => setOpen((v) => !v)}
                        aria-expanded={open}
                        aria-controls="floating-cart-panel"
                    >
                        장바구니 {items.length} • {total.toLocaleString()}원
                    </Button>
                </div>

                <div
                    id="floating-cart-panel"
                    className={`transition-[max-height,opacity,margin-top] duration-200 overflow-hidden rounded-xl border border-border dark:border-dark-border bg-surface dark:bg-dark-surface shadow-lg ${open ? 'opacity-100 max-h-[70vh] mt-2' : 'opacity-0 max-h-0 mt-0'
                        }`}
                    role="dialog"
                    aria-label="빠른 장바구니"
                >
                    <div className="w-[420px] max-h-[70vh] flex flex-col">
                        <header className="px-4 py-3 border-b border-border/60 dark:border-dark-border/60">
                            <h2 className="text-sm font-medium text-text-secondary dark:text-dark-text-secondary">빠른 장바구니</h2>
                        </header>

                        <div className="flex-1 overflow-y-auto px-3 py-3 space-y-2">
                            {items.map((it) => (
                                <ProductLineItemCard
                                    key={`float-${it.productId}`}
                                    item={{ ...it, disableQuantityControls: false, showDeleteButton: true }}
                                    onQuantityChange={(q) => updateQty(it.productId, q)}
                                    onDelete={() => remove(it.productId)}
                                />
                            ))}
                        </div>

                        <footer className="px-4 py-3 border-t border-border/60 dark:border-dark-border/60">
                            <div className="flex items-center justify-between mb-2">
                                <span className="text-text-secondary dark:text-dark-text-secondary">합계</span>
                                <span className="font-semibold text-text-primary dark:text-dark-text-primary ">{total.toLocaleString()}원</span>
                            </div>
                            <div className="flex gap-2">
                                <Button variant="outline" className="flex-1" onClick={() => (window.location.href = '/cart')}>
                                    장바구니로
                                </Button>
                                <Button className="flex-1" onClick={() => (window.location.href = '/checkout')} disabled={items.length === 0}>
                                    바로결제
                                </Button>
                            </div>
                        </footer>
                    </div>
                </div>
            </div>
        </div>
    );

    return createPortal(panel, document.body);
}
