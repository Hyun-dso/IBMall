// /components/product/ProductBuyButtons.tsx
'use client';

import { useRouter } from 'next/navigation';
import type { ProductBuyProps } from '@/types/product';

type SingleModeProps = ProductBuyProps & {
    mode?: 'single';
    className?: string;
    disabled?: boolean;
    label?: string;
    onBuyNow?: (productId: number) => void; // 필요 시 단일 결제 핸들러 전달
};

type CartModeProps = {
    mode: 'cart';
    className?: string;
    disabled?: boolean;
    label?: string;
    onBuyNow: () => void; // 장바구니 결제 트리거
};

type Props = SingleModeProps | CartModeProps;

export default function ProductBuyButtons(props: Props) {
    const router = useRouter();

    // 장바구니 모드
    if (props.mode === 'cart') {
        const { className, disabled, label = '결제하기', onBuyNow } = props;
        return (
            <button className={className} disabled={disabled} onClick={onBuyNow} aria-label="장바구니 결제">
                {label}
            </button>
        );
    }

    // 단일 결제 모드(기본값) — 기존 동작 유지
    const { productId, className, disabled, label = '바로구매', onBuyNow } = props;
    const handleBuy = () => {
        if (onBuyNow) {
            onBuyNow(productId);
            return;
        }
        // 기존 라우팅 유지
        router.push(`/payments/guest/single?productId=${productId}`);
    };

    return (
        <button className={className} disabled={disabled} onClick={handleBuy} aria-label="바로구매">
            {label}
        </button>
    );
}
