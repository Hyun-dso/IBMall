export type Product = {
    description: string; // ← 추가
    productId: number;
    name: string;
    price: number;
    thumbnailUrl: string | null;
    isTimeSale?: boolean;
    timeSalePrice?: number;
    isWished?: boolean;
}


export interface ProductBuyProps {
    productId: number;
    quantity?: number;
    mode?: string | null;
}