// /app/seller/categories/page.tsx
import { requireRole } from '@/lib/auth/role.server';
import { fetchAdminCategories, fetchSellerCategories } from '@/lib/api/seller-categories.server';
import SellerCategoryCreateForm from '@/components/seller/ProductCreateForm';

export const dynamic = 'force-dynamic';

export default async function SellerCategoriesPage() {
    await requireRole('SELLER', '/signin');
    const list = await fetchAdminCategories();

    return (
        <main className="max-w-screen-xl mx-auto px-6 py-8">
            <h1 className="text-lg font-semibold mb-4">카테고리 관리</h1>
            <SellerCategoryCreateForm initialCategories={[]} />
            <section className="mt-6 border border-border dark:border-dark-border rounded-xl overflow-x-auto bg-surface dark:bg-dark-surface">
                <table className="w-full text-sm">
                    <thead>
                        <tr className="text-left border-b border-border dark:border-dark-border">
                            <th className="px-4 py-3">ID</th>
                            <th className="px-4 py-3">이름</th>
                        </tr>
                    </thead>
                    <tbody>
                        {(list ?? []).map((c) => (
                            <tr key={c.id} className="border-t border-border/50 dark:border-dark-border/50">
                                <td className="px-4 py-3">{c.id}</td>
                                <td className="px-4 py-3">{c.name}</td>
                            </tr>
                        ))}
                        {(!list || list.length === 0) && (
                            <tr>
                                <td colSpan={2} className="px-4 py-6 text-center text-text-secondary dark:text-dark-text-secondary">
                                    표시할 카테고리가 없습니다.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </section>
        </main>
    );
}
