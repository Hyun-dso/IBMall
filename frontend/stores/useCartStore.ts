// /stores/useCartStore.ts
'use client';

import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import type { CartItem, CartItemInput } from '@/types/cart';
import { normalizeCartItem, unitPriceOf } from '@/types/cart';

type OptId = number | null;
const keyOf = (pid: number, opt: OptId) => `${pid}:${opt ?? 'none'}`;

function dedupMerge(items: CartItem[]): CartItem[] {
  const m = new Map<string, CartItem>();
  for (const raw of items) {
    const it = normalizeCartItem(raw);
    const k = keyOf(it.productId, it.productOptionId);
    const prev = m.get(k);
    m.set(k, prev ? { ...prev, quantity: prev.quantity + it.quantity } : it);
  }
  return [...m.values()];
}

interface CartState {
  items: CartItem[];
  add: (input: CartItemInput) => void;
  addMany: (list: CartItemInput[], mode?: 'sum' | 'replace') => void;
  updateQty: (productId: number, qty: number, productOptionId?: OptId) => void;
  remove: (productId: number, productOptionId?: OptId) => void;
  clear: () => void;
  hydrateFromSSR?: (items: CartItem[], merge?: boolean) => void;
  computeTotal: () => number;
  countLines: () => number;
}

type Persisted = Pick<CartState, 'items'>;

export const useCartStore = create<CartState>()(
  persist<CartState, [], [], Persisted>(
    (set, get) => ({
      // 1) widen 방지
      items: [] as CartItem[],

      add: (input) => {
        const it = normalizeCartItem(input);           // CartItem 확정
        const next: CartItem[] = get().items.slice();  // 배열 타입 고정
        const k = keyOf(it.productId, it.productOptionId);
        const idx = next.findIndex((x) => keyOf(x.productId, x.productOptionId) === k);

        if (idx >= 0) {
          // 2) 협소화
          const prev = next[idx]!;
          // 3) 결과 봉인
          next[idx] = {
            ...prev,
            quantity: prev.quantity + it.quantity,
          } satisfies CartItem;
        } else {
          next.push(it);
        }
        set({ items: next });
      },

      addMany: (list, mode = 'sum') => {
        if (!list.length) return;
        const norm = list.map(normalizeCartItem);
        if (mode === 'replace') {
          set({ items: dedupMerge(norm) });
        } else {
          set({ items: dedupMerge([...get().items, ...norm]) });
        }
      },

      updateQty: (productId, qty, productOptionId = null) => {
        if (qty <= 0) return;
        const k = keyOf(productId, productOptionId);
        const next = get().items.map((i) =>
          keyOf(i.productId, i.productOptionId) === k ? ({ ...i, quantity: qty } satisfies CartItem) : i
        );
        set({ items: next });
      },

      remove: (productId, productOptionId = null) => {
        const k = keyOf(productId, productOptionId);
        set({ items: get().items.filter((i) => keyOf(i.productId, i.productOptionId) !== k) });
      },

      clear: () => set({ items: [] }),

      hydrateFromSSR: (items, merge = true) => {
        const norm = items.map(normalizeCartItem);
        set({ items: merge ? dedupMerge([...norm, ...get().items]) : dedupMerge(norm) });
      },

      computeTotal: () => get().items.reduce((s, i) => s + unitPriceOf(i) * i.quantity, 0),
      countLines: () => get().items.length,
    }),
    {
      name: 'cart_state_v2',
      storage: createJSONStorage(() => localStorage),
      partialize: (s) => ({ items: s.items }),
      version: 2,
      migrate: (persisted: any) => {
        const items: CartItem[] = Array.isArray(persisted?.items)
          ? persisted.items.map(normalizeCartItem)
          : [];
        return { items: dedupMerge(items) } as Persisted;
      },
    }
  )
);
