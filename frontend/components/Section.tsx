// /components/Section.tsx

'use client';

import ProductCard from '@/components/product/ProductCard';
import type { Product } from '@/types/product';

interface SectionProps {
    title: string;
    products: Product[];
}

export default function Section({ title, products }: SectionProps) {
    if (!products || products.length === 0) return null;

    return (
        <section className="mt-16 max-w-screen-xl">

            <h2 className="text-xl font-bold mb-4 text-center">
                {title}
            </h2>
            <div className="max-w-screen-xl mx-auto px-6 flex w-fit">
                <div className="flex flex-wrap gap-6 justify-center mb-8">
                    {products.map((product) => (
                        <ProductCard
                            key={product.productId}
                            {...product}
                            isWished={product.isWished ?? false}
                        />
                    ))}
                </div>
            </div>
        </section>
    );
}
