// /types/product.ts

export interface Product {
    productId: number;
    name: string;
    price: number;
    thumbnailUrl: string | null;
    description: string; // ← 추가
    isTimeSale?: boolean;
    timeSalePrice?: number;
    isWished?: boolean;
}

export interface ProductBuyProps {
    productId: number;
    quantity?: number;
}