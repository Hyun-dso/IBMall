// /components/Section.tsx

'use client';

import ProductCard from '@/components/ProductCard';
import type { Product } from '@/types/product';

interface SectionProps {
    title: string;
    products: Product[];
}

export default function Section({ title, products }: SectionProps) {
    if (!products || products.length === 0) return null;

    return (
        <section className="mb-16">
            <div className="w-full px-4">
                <h2 className="text-xl font-bold mb-4 text-text-primary dark:text-dark-text-primary text-center">
                    {title}
                </h2>
                <div className="w-full flex justify-center">
                    <div className="grid gap-6 grid-cols-[repeat(auto-fit,minmax(240px,1fr))] w-full max-w-screen-xl">
                        {products.map((product) => (
                            <ProductCard
                                key={product.productId}
                                {...product}
                                isWished={product.isWished ?? false}
                            />
                        ))}
                    </div>
                </div>
            </div>
        </section>
    );
}
