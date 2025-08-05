// /types/product.ts
export interface Product {
    productId: number;
    name: string;
    price: number;
    stock: number;
    categoryId: number;
    description: string;
    imageUrl: string | null;
    thumbnailUrl: string | null; // 누락된 필드 추가
    createdAt: string;
    viewCount: number;
    salesCount: number;
    isTimeSale: boolean;
    timeSalePrice: number | null;
}
