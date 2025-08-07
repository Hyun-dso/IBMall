// /app/products/[productId]/page.tsx

import type { Product } from '@/types/product';
import Image from 'next/image';
import { notFound } from 'next/navigation';
import Button from '@/components/ui/Button';
import ProductBuyButtons from '@/components/product/ProductBuyButtons';

interface Params {
    params: { productId: string };
}

async function fetchProductById(id: string): Promise<Product | null> {
    const baseUrl = process.env.API_BASE_URL;
    if (!baseUrl) return null;

    try {
        const res = await fetch(`${baseUrl}/api/products/${id}`, { cache: 'no-store' });
        if (!res.ok) return null;

        const json = await res.json();
        return json.data?.product ?? null;
    } catch {
        return null;
    }
}

export default async function ProductPage({ params }: Params) {
    const product = await fetchProductById(params.productId);
    if (!product) return notFound();

    const displayPrice =
        product.isTimeSale && product.timeSalePrice
            ? product.timeSalePrice
            : product.price;

    return (
        <main className="mt-32 scroll-mt-32 px-6 pb-16 bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary min-h-screen">
            <div className="w-full max-w-screen-xl mx-auto flex flex-col md:flex-row gap-12">
                <div className="w-full md:w-1/2 aspect-square relative rounded border border-border dark:border-dark-border">
                    <Image
                        src={product.thumbnailUrl ?? '/img0.jpg'}
                        alt={product.name}
                        fill
                        className="object-cover rounded"
                    />
                </div>

                <div className="flex-1">
                    <h1 className="text-2xl font-bold mb-2">
                        {product.name}
                    </h1>

                    <div className="text-lg font-semibold mb-4">
                        {product.isTimeSale && product.timeSalePrice ? (
                            <>
                                <span className="line-through text-sm text-text-secondary dark:text-dark-text-secondary mr-2">
                                    {product.price.toLocaleString()}원
                                </span>
                                <span className="text-error">
                                    {product.timeSalePrice.toLocaleString()}원
                                </span>
                            </>
                        ) : (
                            `${product.price.toLocaleString()}원`
                        )}
                    </div>

                    <p className="text-sm text-text-secondary dark:text-dark-text-secondary leading-relaxed mb-6">
                        {product.description}
                    </p>

                    <div className="flex gap-2">
                        <Button variant="outline" size="md">
                            장바구니
                        </Button>
                        <ProductBuyButtons productId={product.productId} />
                    </div>
                </div>
            </div>
        </main >
    );
}
