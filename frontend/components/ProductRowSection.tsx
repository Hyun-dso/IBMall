// /components/ProductRowSection.tsx

'use client';

import ProductCard from '@/components/ProductCard';
import type { Product } from '@/types/product';

interface Props {
    title: string;
    products: Product[];
}

export default function ProductRowSection({ title, products }: Props) {
    return (
        <section className="mb-12">
            <h2 className="text-xl font-semibold mb-4 px-4">{title}</h2>
            <div
                className="flex gap-4 overflow-x-auto px-4 scroll-smooth"
                style={{ scrollSnapType: 'x mandatory' }}
            >
                {products.map((product) => (
                    <div
                        key={product.productId}
                        className="flex-shrink-0 snap-start w-[240px]"
                    >
                        <ProductCard {...product} />
                    </div>
                ))}
            </div>
        </section>
    );
}
