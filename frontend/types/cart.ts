// /types/cart.ts
import type { Id, Won } from './primitives';

export interface CartItem {
  productId: Id;
  name: string;
  price: Won;
  quantity: number;
  thumbnailUrl: string | null;
  productOptionId?: Id | null;
}
export interface ProductLineItem extends CartItem {}
