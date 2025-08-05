// /app/page.tsx
import { getTimeSaleProducts, getNewProducts, getPopularProducts, getRecommendedProducts } from '@/lib/products';
import ProductGrid from '@/components/ui/ProductGrid';
import SectionTitle from '@/components/ui/SectionTitle';

export default async function HomePage() {
    const [timeSale, newProducts, popular, recommended] = await Promise.all([
        getTimeSaleProducts(),
        getNewProducts(),
        getPopularProducts(),
        getRecommendedProducts(),
    ]);

    return (
        <main className="w-full bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
            <section className="w-full h-80 bg-primary text-white flex justify-center items-center">
                <h1 className="text-3xl font-bold">IBMall에 오신 걸 환영합니다</h1>
            </section>

            <div className="max-w-screen-xl mx-auto px-4 py-8 space-y-12">
            </div>
        </main>
    );
}
