// /types/cart.ts
export type Nullable<T> = T | null;

/**
 * 스토어에 저장되는 장바구니 단위. undefined 금지.
 * UI 전용 플래그는 포함하지 않는다.
 */
export interface CartItem {
    productId: number;
    productOptionId: Nullable<number>;   // 옵션 없으면 null
    name: string;
    price: number;                       // 기본 단가(옵션가 미포함)
    isTimeSale: boolean;
    timeSalePrice: Nullable<number>;     // 타임세일 단가 없으면 null
    thumbnailUrl: Nullable<string>;
    optionName: Nullable<string>;
    quantity: number;                    // 1 이상 보장
}

/**
 * UI 표시용 라인 아이템. 스토어 아이템 + UI 전용 플래그.
 * 이 타입은 스토어에 저장하지 말 것.
 */
export interface ProductLineItem extends CartItem {
    disableQuantityControls: boolean;
    showDeleteButton: boolean;
    paidAmount?: number; // 사용한다면 계산 값(파생). 스토어 저장 금지.
}

export interface CheckoutCartItem extends CartItem {
    productOptionId: number;
}

/**
 * 바깥에서 add 시 넘길 수 있는 느슨한 입력 형식.
 * undefined 허용, factory가 정규화한다.
 */
export type CartItemInput =
    Pick<CartItem, 'productId' | 'name' | 'price' | 'quantity'> &
    Partial<Pick<CartItem, 'productOptionId' | 'isTimeSale' | 'timeSalePrice' | 'thumbnailUrl' | 'optionName'>>;

/** 공통 유틸: 실제 적용 단가 */
export function unitPriceOf(i: CartItem): number {
    return i.isTimeSale && i.timeSalePrice != null ? i.timeSalePrice : i.price;
}

/** 입력 정규화: undefined 제거, 수량 하한, nullables 채움 */
export function normalizeCartItem(input: CartItemInput): CartItem {
    return {
        productId: input.productId,
        name: input.name,
        price: input.price,
        quantity: Math.max(1, input.quantity),
        productOptionId: input.productOptionId ?? null,
        isTimeSale: input.isTimeSale ?? false,
        timeSalePrice: input.timeSalePrice ?? null,
        thumbnailUrl: input.thumbnailUrl ?? null,
        optionName: input.optionName ?? null,
    };
}
