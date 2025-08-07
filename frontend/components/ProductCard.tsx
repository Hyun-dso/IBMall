// /components/ProductCard.tsx

'use client';

import Image from 'next/image';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { toast } from 'react-hot-toast';
import { useState } from 'react';
import { Heart } from 'lucide-react';
import Button from '@/components/ui/Button';

interface Props {
    productId: number;
    name: string;
    price: number;
    thumbnailUrl: string | null;
    isTimeSale?: boolean;
    timeSalePrice?: number;
    isWished?: boolean; // optional 초기값
}

export default function ProductCard({
    productId,
    name,
    price,
    thumbnailUrl,
    isTimeSale,
    timeSalePrice,
    isWished = false,
}: Props) {
    const router = useRouter();
    const [wished, setWished] = useState(isWished);
    const displayPrice = isTimeSale && timeSalePrice ? timeSalePrice : price;

    const handleWishlist = async () => {
        try {
            const res = await fetch(`/api/wishlist/${productId}`, {
                method: wished ? 'DELETE' : 'POST',
                credentials: 'include',
            });

            if (!res.ok) {
                const { message } = await res.json();
                toast.error(message || '처리 실패');
                return;
            }

            setWished(!wished);
            toast.success(wished ? '위시리스트에서 제거됨' : '위시리스트에 추가됨');
        } catch {
            toast.error('위시리스트 처리 중 오류 발생');
        }
    };

    const handleAddToCart = async () => {
        try {
            const res = await fetch('/api/cart', {
                method: 'POST',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ productId, quantity: 1 }),
            });

            if (!res.ok) {
                const { message } = await res.json();
                toast.error(message || '장바구니 담기 실패');
                return;
            }

            toast.success('장바구니에 담겼습니다');
        } catch {
            toast.error('장바구니 처리 중 오류 발생');
        }
    };

    const handleBuyNow = async () => {
        try {
            const res = await fetch('/api/payments/guest/single', {
                method: 'POST',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ productId, quantity: 1 }),
            });

            if (!res.ok) {
                const { message } = await res.json();
                toast.error(message || '구매 실패');
                return;
            }

            const { redirectUrl } = await res.json();
            router.push(redirectUrl);
        } catch {
            toast.error('구매 처리 중 오류 발생');
        }
    };

    return (
        <div className="border border-border dark:border-dark-border bg-surface dark:bg-dark-surface p-4 rounded-md flex flex-col overflow-hidden shadow transition-transform duration-200 hover:scale-[1.02]">
            <div className="relative aspect-square mb-2">
                <Link href={`/products/${productId}`}>
                    {thumbnailUrl ? (
                        <Image
                            src={thumbnailUrl}
                            alt={name}
                            fill
                            className="object-cover rounded dark:brightness-75"
                        />
                    ) : (
                        <div className="w-full h-full flex items-center justify-center text-sm text-text-secondary dark:text-dark-text-secondary bg-background dark:bg-dark-background rounded">
                            이미지 없음
                        </div>
                    )}
                </Link>
                <button
                    type="button"
                    onClick={handleWishlist}
                    className="absolute top-2 right-2 z-10"
                >
                    <Button variant="ghost" size="icon">
                        <Heart
                            size={18}
                            className={wished ? 'fill-primary stroke-primary' : 'stroke-text-secondary dark:stroke-dark-text-secondary'}
                        />
                    </Button>
                </button>
            </div>

            <h3 className="text-base font-semibold truncate">{name}</h3>

            {isTimeSale && timeSalePrice && (
                <p className="text-sm text-text-secondary dark:text-dark-text-secondary line-through">
                    {price.toLocaleString()}원
                </p>
            )}
            <p className="text-lg font-bold text-primary">{displayPrice.toLocaleString()}원</p>

            <div className="mt-3 flex gap-2">
                <Button variant="outline" size="sm" className="flex-1" onClick={handleAddToCart}>
                    장바구니
                </Button>
                <Button
                    variant="solid"
                    size="md"
                    onClick={() => {
                        router.push(`/pay?productId=${productId}&quantity=1`);
                    }}
                >
                    바로구매
                </Button>
            </div>
        </div>
    );
}
