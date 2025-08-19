// /components/ProductGrid.tsx
import ProductCard from '../ProductCard';

interface Product {
    productId: number;
    name: string;
    price: number;
    thumbnailUrl: string | null;
    description?: string;
    isTimeSale?: boolean;
    timeSalePrice?: number;
}

interface Props {
    products: Product[];
}

export default function ProductGrid({ products }: Props) {
    if (!products || products.length === 0) return <p className="text-text-secondary dark:text-dark-text-secondary">상품이 없습니다.</p>;

    return (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
            {products.map((product) => (
                <ProductCard key={product.productId} {...product} />
            ))}
        </div>
    );
}