// /app/seller/products/new/page.tsx
import { requireRole } from '@/lib/auth/role.server';
import { fetchSellerCategories } from '@/lib/api/seller-categories.server';
import ProductCreateForm from '@/components/seller/ProductCreateForm'; // 올바른 컴포넌트

export const dynamic = 'force-dynamic';

export default async function SellerProductNewPage() {
    await requireRole('SELLER', '/signin');

    const categories = (await fetchSellerCategories()) ?? [];

    return (
        <main className="mt-[var(--header-height)] max-w-screen-xl mx-auto px-6 py-8">
            <h1 className="text-lg font-semibold mb-4">새 상품 등록</h1>
            <ProductCreateForm initialCategories={categories} /> {/* 필수 prop 전달 */}
        </main>
    );
}
