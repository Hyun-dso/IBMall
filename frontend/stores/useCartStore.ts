// /stores/useCartStore.ts
import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import type { CartItem } from '@/types/cart';

interface CartState {
    items: CartItem[];
    add: (item: CartItem) => void;
    remove: (productId: number) => void;
    updateQty: (productId: number, qty: number) => void;
    clear: () => void;
    hydrateFromSSR?: (items: CartItem[], merge?: boolean) => void;
}

export const useCartStore = create<CartState>()(
    persist(
        (set, get) => ({
            items: [],
            add: (item) => {
                const curr = get().items.slice();
                const idx = curr.findIndex((i) => i.productId === item.productId);
                if (idx >= 0) curr[idx] = { ...curr[idx], quantity: curr[idx].quantity + item.quantity };
                else curr.push(item);
                set({ items: curr });
            },
            remove: (productId) => set({ items: get().items.filter((i) => i.productId !== productId) }),
            updateQty: (productId, qty) => {
                if (qty <= 0) return;
                set({ items: get().items.map((i) => (i.productId === productId ? { ...i, quantity: qty } : i)) });
            },
            clear: () => set({ items: [] }),
            hydrateFromSSR: (items, merge = true) => {
                const curr = get().items;
                set({
                    items: merge
                        ? [...items, ...curr.filter((c) => !items.some((m) => m.productId === c.productId))]
                        : items,
                });
            },
        }),
        {
            name: 'cart_state_v1',
            storage: createJSONStorage(() => localStorage),
            partialize: (s) => ({ items: s.items }),
            version: 1,
        }
    )
);
