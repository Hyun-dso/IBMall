// /components/ProductCard.tsx
'use client';

import Image from 'next/image';
import Link from 'next/link';
import { useMemo, useState } from 'react';
import { useCartStore } from '@/stores/useCartStore';
import type { CartItem } from '@/types/cart';
import { showToast } from '@/lib/toast';
import { Product } from '@/types/product';
import { useRouter } from 'next/navigation';
import ProductBuyButtons from '../ProductBuyButtons';

function buildCartItem(p: Product): CartItem {
    return {
        productId: p.productId,
        name: p.name,
        thumbnailUrl: p.thumbnailUrl ?? null,
        price: p.price,
        quantity: 1,
        isTimeSale: !!p.isTimeSale,
        timeSalePrice: p.timeSalePrice ?? null,
        optionName: null,
        productOptionId: null, // 옵션 미선택 상태를 명시적으로 null
    };
}

// 회원 장바구니 서버 동기화 훅 포인트
async function trySyncMemberCartAfterAdd(_item: CartItem) {
    // TODO: 회원 장바구니 API 명세 확정 시 연동
    return;
}

export default function ProductCard(props: Product) {
    const router = useRouter();  // 클라이언트 사이드에서만 동작
    const { productId, name, price, thumbnailUrl, isTimeSale, timeSalePrice, isWished = false } = props;

    const [wished, setWished] = useState(isWished);
    const [adding, setAdding] = useState(false);

    const displayPrice = useMemo(
        () => (isTimeSale && timeSalePrice ? timeSalePrice : price),
        [isTimeSale, timeSalePrice, price]
    );

    const add = useCartStore((s) => s.add);

    const handleWishlist = async () => {
        const group = `wish-${productId}`;
        try {
            const res = await fetch(`/api/wishlist/${productId}`, {
                method: wished ? 'DELETE' : 'POST',
                credentials: 'include',
            });
            if (!res.ok) {
                let msg = '처리 실패';
                try {
                    const j = await res.json();
                    msg = j.message || msg;
                } catch { }
                showToast.error(msg, { group });
                return;
            }
            setWished(!wished);
            showToast.success(wished ? '위시리스트에서 제거했어요' : '위시리스트에 추가했어요', { group });
        } catch {
            showToast.error('위시리스트 처리 중 오류가 발생했어요', { group });
        }
    };

    const handleAddToCart = async () => {
        if (adding) return;
        setAdding(true);

        const group = `cart-${productId}`;
        try {
            const item = buildCartItem(props);
            add(item); // zustand persist 자동 저장
            showToast.success('장바구니에 담았어요', { group });
            await trySyncMemberCartAfterAdd(item);
        } catch {
            showToast.error('서버 동기화에 실패했어요', { group });
        } finally {
            setAdding(false);
        }
    };


    const handleBuy = () => {
        // 기존 라우팅 유지
        router.push(`/payments/guest/single?productId=${productId}`);
    };

    return (
        <div className="min-w-64 border border-[var(--border)] bg-[var(--background)] p-4 rounded-md flex flex-col overflow-hidden shadow transition-transform duration-200 hover:border-[var(--primary)]">
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
                        <div className="w-full h-full flex items-center justify-center text-sm text-[var(--foreground-secondary)] bg-[var(--surface)] rounded">
                            이미지 없음
                        </div>
                    )}
                </Link>
                <button type="button" onClick={handleWishlist} className="absolute top-2 right-2 z-10">
                    {/* <Button variant="ghost" size="icon" aria-label="위시리스트 토글">
                        <Heart
                            size={18}
                            className={
                                wished
                                    ? 'fill-primary stroke-primary'
                                    : 'stroke-text-secondary dark:stroke-dark-text-secondary'
                            }
                        />
                    </Button> */}
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
                <button
                    className="flex-1 border border-[var(--foreground)] rounded-md p-2 hover:cursor-pointer"
                    onClick={handleAddToCart}
                    disabled={adding}
                    aria-busy={adding}
                >
                    {adding ? '담는 중...' : '장바구니'}
                </button>

                {/* 의도 기반 바로구매: 내부에서 intent 생성 후 /payments/{role}/single?intent=...로 이동 */}
                <ProductBuyButtons mode='single' className="flex-1" productId={productId} />
            </div>
        </div>
    );
}
