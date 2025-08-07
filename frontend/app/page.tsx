import Section from '@/components/Section';
import type { Product } from '@/types/product';
// import FloatingActivityBar from '@/components/FloatingActivityBar';

async function fetchProducts(query: string): Promise<Product[]> {
    const baseUrl = process.env.API_BASE_URL;
    if (!baseUrl) return [];

    try {
        const res = await fetch(`${baseUrl}/api/products?${query}`, { cache: 'no-store' });
        if (!res.ok) return [];
        const json = await res.json();
        return json.data || [];
    } catch {
        return [];
    }
}

export default async function HomePage() {
    const [timeSaleItems, newestItems, popularItems, featuredItems, allItems] = await Promise.all([
        fetchProducts('isTimeSale=true'),
        fetchProducts('sort=created_at&order=desc&limit=8'),
        fetchProducts('sort=views&order=desc&limit=8'),
        fetchProducts('featured=true'),
        fetchProducts(''), // 전체 상품
    ]);

    return (
        <div className="bg-white dark:bg-dark-background min-h-screen text-black dark:text-white">
            <main className="mt-24 relative z-10 mb-64">
                {/* <FloatingActivityBar /> */}
                <Section title="오늘의 특가" products={timeSaleItems} />
                <Section title="신상품" products={newestItems} />
                <Section title="인기 상품" products={popularItems} />
                <Section title="추천 상품" products={featuredItems} />

                {/* <ProductGridLayout title="전체 상품">
                    {allItems.map((product) => (
                        <ProductCard key={product.productId} {...product as Props} />
                    ))}
                </ProductGridLayout> */}

                <div className="h-64 bg-opacity-0" />
            </main>
        </div>
    );
}
