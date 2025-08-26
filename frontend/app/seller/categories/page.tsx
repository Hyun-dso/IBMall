import { listCategoriesServer } from '@/lib/api/seller-categories';
import CategoryClient from './parts/CategoryClient';
import type { Category } from '@/types/catalog';

export const dynamic = 'force-dynamic';

export default async function SellerCategoriesPage() {
    const res = await listCategoriesServer();
    const categories: Category[] = res.ok ? res.data : [];

    return (
        <div className="space-y-4">
            <h1 className="text-2xl font-bold">카테고리 관리</h1>
            <CategoryClient
                initialCategories={categories}
                loadError={res.ok ? null : res.message}
            />
        </div>
    );
}
