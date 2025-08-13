// /stores/useCartStore.ts
'use client';

import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { CartItem } from '@/types/cart';

interface CartState {
    items: CartItem[];
    add: (item: CartItem) => void;
    remove: (productId: number, productOptionId?: number | null) => void;
    updateQty: (productId: number, qty: number, productOptionId?: number | null) => void;
    clear: () => void;
}

export const useCartStore = create<CartState>()(
    persist(
        (set, get) => ({
            items: [],
            add: (item) => {
                const exists = get().items.find(
                    (i) => i.productId === item.productId && i.productOptionId === item.productOptionId
                );
                if (exists) {
                    set({
                        items: get().items.map((i) =>
                            i.productId === item.productId && i.productOptionId === item.productOptionId
                                ? { ...i, quantity: i.quantity + item.quantity }
                                : i
                        )
                    });
                } else {
                    set({ items: [...get().items, item] });
                }
            },
            remove: (productId, productOptionId = null) =>
                set({
                    items: get().items.filter(
                        (i) => !(i.productId === productId && (i.productOptionId ?? null) === productOptionId)
                    )
                }),
            updateQty: (productId, qty, productOptionId = null) =>
                set({
                    items: get().items.map((i) =>
                        i.productId === productId && (i.productOptionId ?? null) === productOptionId
                            ? { ...i, quantity: qty }
                            : i
                    )
                }),
            clear: () => set({ items: [] })
        }),
        { name: 'guest-cart' }
    )
);
