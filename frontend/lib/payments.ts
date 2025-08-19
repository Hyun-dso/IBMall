// /lib/payments.ts
import type { CartItem, CheckoutCartItem } from '@/types/cart';
import type { GuestCartPaymentItem } from '@/types/payment';

function assertCheckoutItems(items: CartItem[]): asserts items is CheckoutCartItem[] {
  const missing = items.find((it) => typeof it.productOptionId !== 'number');
  if (missing) throw new Error(`옵션 정보 누락: productId=${missing.productId}`);
}

export function mapItems(items: CartItem[]): GuestCartPaymentItem[] {
  assertCheckoutItems(items); // 여기서 CheckoutCartItem[]로 좁혀짐
  return items.map((it) => ({
    productId: it.productId,
    productOptionId: it.productOptionId, // 이제 number
    productName: it.name,
    quantity: it.quantity,
    price: it.isTimeSale && it.timeSalePrice ? it.timeSalePrice : it.price,
  }));
}
