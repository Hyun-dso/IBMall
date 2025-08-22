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
    imageUrls?: string[];         // 옵셔널, undefined 금지(키 생략)
}

export interface ProductBuyProps {
    productId: number;
    quantity?: number;
    mode: string | null;
}