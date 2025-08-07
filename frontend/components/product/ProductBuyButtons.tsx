// /components/product/ProductBuyButtons.tsx
'use client';

import { useRouter } from 'next/navigation';
import Button from '@/components/ui/Button';
import type { ProductBuyProps } from '@/types/product';

export default function ProductBuyButtons({ productId, quantity = 1 }: ProductBuyProps) {
    const router = useRouter();

    const handleBuy = () => {
        router.push(`/pay?productId=${productId}&quantity=${quantity}`);
    };

    return (
        <div className="flex gap-2">
            <Button variant="outline" size="md">장바구니</Button>
            <Button variant="solid" size="md" onClick={handleBuy}>
                바로구매
            </Button>
        </div>
    );
}
