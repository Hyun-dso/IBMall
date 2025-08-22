// /lib/products.ts
import type { Product } from '@/types/product';

export async function getTimeSaleProducts(): Promise<Product[]> {
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/products?isTimeSale=true`, {
        cache: 'no-store',
        credentials: 'include',
    });

    if (!res.ok) throw new Error('Failed to fetch time sale products');
    return res.json();
}

export async function getNewProducts(): Promise<Product[]> {
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/products?sort=created_at_desc`, {
        cache: 'no-store',
        credentials: 'include',
    });

    if (!res.ok) throw new Error('Failed to fetch new products');
    return res.json();
}

export async function getPopularProducts(): Promise<Product[]> {
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/products?sort=popular`, {
        cache: 'no-store',
        credentials: 'include',
    });

    if (!res.ok) throw new Error('Failed to fetch popular products');
    return res.json();
}

export async function getRecommendedProducts(): Promise<Product[]> {
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/products?sort=recommended`, {
        cache: 'no-store',
        credentials: 'include',
    });

    if (!res.ok) throw new Error('Failed to fetch recommended products');
    return res.json();
}
