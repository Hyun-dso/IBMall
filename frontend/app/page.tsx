// /app/page.tsx
'use client';

import { useEffect, useState } from 'react';
import Section from '@/components/Section';
import type { Product } from '@/types/product';

async function fetchProducts(query: string): Promise<Product[]> {
    const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL; // 환경 변수 키 확인
    if (!baseUrl) return [];

    try {
        const res = await fetch(`${baseUrl}/api/products?${query}`, {
            cache: 'no-store',
        });
        if (!res.ok) return [];
        const json = await res.json();
        return json.data || [];
    } catch {
        return [];
    }
}

export default function HomePage() {
    const [timeSaleItems, setTimeSaleItems] = useState<Product[]>([]);
    const [newestItems, setNewestItems] = useState<Product[]>([]);
    const [popularItems, setPopularItems] = useState<Product[]>([]);
    const [featuredItems, setFeaturedItems] = useState<Product[]>([]);

    useEffect(() => {
        Promise.all([
            fetchProducts('isTimeSale=true'),
            fetchProducts('sort=created_at&order=desc&limit=8'),
            fetchProducts('sort=views&order=desc&limit=8'),
            fetchProducts('featured=true'),
        ]).then(([timeSale, newest, popular, featured]) => {
            setTimeSaleItems(timeSale);
            setNewestItems(newest);
            setPopularItems(popular);
            setFeaturedItems(featured);
        });
    }, []);

    return (
        <div className="bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary min-h-screen">
            {/* <Section title="오늘의 특가" products={timeSaleItems} /> */}
            <Section title="신상품" products={newestItems} />
            {/* <Section title="인기 상품" products={popularItems} /> */}
            {/* <Section title="추천 상품" products={featuredItems} /> */}
        </div>
    );
}
