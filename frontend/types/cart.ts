// /types/cart.ts
export interface CartItem {
    productId: number;
    name: string;
    price: number;
    quantity: number;
    thumbnailUrl: string | null;
    isTimeSale?: boolean;
    timeSalePrice?: number | null;
    optionName?: string | null;
    productOptionId?: number; // 장바구니 단계에서는 선택 전일 수 있으므로 optional
    disableQuantityControls?: boolean;
    showDeleteButton?: boolean;
}

export type ProductLineItem = CartItem & {
    disableQuantityControls?: boolean;
    showDeleteButton?: boolean;
};

// 결제 단계에서 productOptionId가 필수일 때 사용할 별칭
export type CheckoutCartItem = Omit<CartItem, 'productOptionId'> &
    Required<Pick<CartItem, 'productOptionId'>>;

export interface ServerCartPayload {
    items: CartItem[];
    updatedAt: string;
}
