// /app/seller/products/page.tsx
import Link from 'next/link';
import Button from '@/components/ui/Button';
import { listSellerProducts } from '@/lib/api/seller.server';

export const dynamic = 'force-dynamic';

export default async function SellerProductsPage() {
    const res = await listSellerProducts({ page: 1, size: 20 });

    return (
        <div className="space-y-4">
            <div className="flex items-center justify-between">
                <h1 className="text-lg font-semibold">상품 관리</h1>
                <Button asChild size="sm" variant="solid">
                    <Link href="/seller/products/new">새 상품 등록</Link>
                </Button>
            </div>

            <div className="border border-border dark:border-dark-border rounded-xl overflow-x-auto bg-surface dark:bg-dark-surface">
                <table className="w-full text-sm">
                    <thead className="text-left">
                        <tr className="border-b border-border dark:border-dark-border">
                            <th className="px-4 py-3">상품ID</th>
                            <th className="px-4 py-3">상품명</th>
                            <th className="px-4 py-3">가격</th>
                            <th className="px-4 py-3">재고</th>
                            <th className="px-4 py-3">상태</th>
                        </tr>
                    </thead>
                    <tbody>
                        {(res?.items ?? []).map(p => (
                            <tr key={p.id} className="border-t border-border/50 dark:border-dark-border/50">
                                <td className="px-4 py-3">{p.id}</td>
                                <td className="px-4 py-3">{p.name}</td>
                                <td className="px-4 py-3">{p.priceFormatted ?? p.price}</td>
                                <td className="px-4 py-3">{p.stock}</td>
                                <td className="px-4 py-3">{p.status}</td>
                            </tr>
                        ))}
                        {(!res || res.items.length === 0) && (
                            <tr>
                                <td colSpan={5} className="px-4 py-6 text-center text-text-secondary dark:text-dark-text-secondary">
                                    표시할 상품이 없습니다.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
