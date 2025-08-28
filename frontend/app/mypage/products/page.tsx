import { listProductsServer } from '@/lib/api/seller-product';
import { listCategoriesServer } from '@/lib/api/seller-categories';
import type { Category, Product } from '@/types/catalog';
import ProductClient from './parts/ProductClient';

export const dynamic = 'force-dynamic';

export default async function SellerProductsPage() {
    const [prodRes, catRes] = await Promise.all([listProductsServer(), listCategoriesServer()]);
    const products: Product[] = prodRes.ok ? prodRes.data : [];
    const categories: Category[] = catRes.ok ? catRes.data : [];

    return (
        <div className="space-y-4">
            <h1 className="text-2xl font-bold">상품 관리</h1>
            <ProductClient
                initialProducts={products}
                initialCategories={categories}
                loadErrors={{
                    products: prodRes.ok ? null : prodRes.message,
                    categories: catRes.ok ? null : catRes.message,
                }}
            />
        </div>
    );
}
