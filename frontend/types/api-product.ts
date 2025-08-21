import type { ProductStatus } from '@/types/catalog';

export interface CreateProductPayload {
    name: string;
    price: number;
    stock: number;
    categoryId: number;
    status: ProductStatus;
    description: string | null;   // 명시적 null 허용
    imageUrls?: string[];         // 옵셔널, undefined 금지(키 생략)
}

export type UpdateProductPayload = CreateProductPayload; // 스펙 동일