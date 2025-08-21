// /components/ProductCard.tsx

'use client';

import Image from 'next/image';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { toast } from 'react-hot-toast';
import { useMemo, useState } from 'react';
import { Heart } from 'lucide-react';
import Button from '@/components/ui/Button';
import ProductBuyButtons from '@/components/product/ProductBuyButtons';
import { useCartStore } from '@/stores/useCartStore';
import type { CartItem } from '@/types/cart';

interface Props {
    productId: number;
    name: string;
    price: number;
    thumbnailUrl: string | null;
    isTimeSale?: boolean;
    timeSalePrice?: number;
    isWished?: boolean;
}

function buildCartItem(p: Props): CartItem {
    return {
        productId: p.productId,
        name: p.name,
        thumbnailUrl: p.thumbnailUrl ?? null,
        price: p.price,
        quantity: 1,
        isTimeSale: !!p.isTimeSale,
        timeSalePrice: p.timeSalePrice ?? null,
        optionName: null,
        // productOptionId: , // 옵션 미선택 상태 허용
    };
}

// 회원 장바구니 서버 동기화 훅 포인트
async function trySyncMemberCartAfterAdd(_item: CartItem) {
    // TODO: 회원 장바구니 API 명세 확정 시 연동
    return;
}

export default function ProductCard(props: Props) {
    const {
        productId, name, price, thumbnailUrl,
        isTimeSale, timeSalePrice, isWished = false,
    } = props;

    const router = useRouter();
    const [wished, setWished] = useState(isWished);
    const [adding, setAdding] = useState(false);

    const displayPrice = useMemo(
        () => (isTimeSale && timeSalePrice ? timeSalePrice : price),
        [isTimeSale, timeSalePrice, price]
    );

    const add = useCartStore((s) => s.add);

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
        if (adding) return;
        setAdding(true);

        const item = buildCartItem(props);
        add(item); // zustand persist가 자동 영속화

        toast.success('장바구니에 담았습니다');
        try {
            await trySyncMemberCartAfterAdd(item);
        } catch {
            toast.error('서버 동기화 실패. 잠시 후 다시 시도하세요.');
        } finally {
            setAdding(false);
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
                            sizes="(max-width:1024px) 50vw, 25vw"
                            className="object-cover rounded dark:brightness-75"
                        />
                    ) : (
                        <div className="w-full h-full flex items-center justify-center text-sm text-text-secondary dark:text-dark-text-secondary bg-background dark:bg-dark-background rounded">
                            이미지 없음
                        </div>
                    )}
                </Link>
                <button type="button" onClick={handleWishlist} className="absolute top-2 right-2 z-10">
                    <Button variant="ghost" size="icon" aria-label="위시리스트 토글">
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
                <Button
                    variant="outline"
                    size="md"
                    className="flex-1"
                    onClick={handleAddToCart}
                    disabled={adding}
                    aria-busy={adding}
                >
                    {adding ? '담는 중...' : '장바구니'}
                </Button>
                <ProductBuyButtons className="flex-1" productId={productId} />
            </div>
        </div>
    );
}
