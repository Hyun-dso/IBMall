export interface Category {
    id: number;
    name: string;
}

export type ProductStatus = 'ACTIVE' | 'INACTIVE';

export interface Product {
    id: number;
    categoryId: number;
    name: string;
    price: number;
    stock: number;
    description: string | null;
    thumbnailUrl: string | null;
    status: ProductStatus;
    images: string[];
    createdAt: string; // ISO
}
