// /components/cart/FloatingCart.tsx
'use client';

import { useEffect, useMemo, useState } from 'react';
import { createPortal } from 'react-dom';
import { useRouter } from 'next/navigation';
import { useCartStore } from '@/stores/useCartStore';
import Button from '@/components/ui/Button';
import CartPanel from '@/components/cart/CartPanel';

const MIN_DESKTOP_WIDTH = 0;

export default function FloatingCart() {
  const router = useRouter();
  const items = useCartStore((s) => s.items);
  const updateQty = useCartStore((s) => s.updateQty);
  const remove = useCartStore((s) => s.remove);

  const [mounted, setMounted] = useState(false);
  const [open, setOpen] = useState(false);
  const [isDesktop, setIsDesktop] = useState(true);

  const total = useMemo(
    () =>
      items.reduce((acc, cur) => {
        const unit = cur.isTimeSale && cur.timeSalePrice != null ? cur.timeSalePrice : cur.price;
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
          className={[
            'transition-[max-height,opacity,margin-top] duration-200 overflow-hidden',
            'rounded-xl border border-border dark:border-dark-border bg-surface dark:bg-dark-surface shadow-lg',
            open ? 'opacity-100 max-h-[70vh] mt-2' : 'opacity-0 max-h-0 mt-0',
          ].join(' ')}
          role="dialog"
          aria-label="빠른 장바구니"
        >
          <CartPanel
            items={items}
            total={total}
            onQtyChange={updateQty}
            onRemove={remove}
            onCheckout={() => {
              // /payments에서 SSR 분기로 각 role의 /cart로 이동
              // 필요 시 intent 생성 단계로 바꾸면 이 부분만 교체
              window.scrollTo({ top: 0 });
              router.push('/payments');
            }}
          />
        </div>
      </div>
    </div>
  );

  return createPortal(panel, document.body);
}
