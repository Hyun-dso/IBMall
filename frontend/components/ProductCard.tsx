// /components/ProductCard.tsx
import Image from 'next/image';

interface Props {
    productId: number;
    name: string;
    price: number;
    thumbnailUrl: string | null;
    description?: string;
    isTimeSale?: boolean;
    timeSalePrice?: number;
}

export default function ProductCard({ productId, name, price, thumbnailUrl, isTimeSale, timeSalePrice }: Props) {
    const displayPrice = isTimeSale && timeSalePrice ? timeSalePrice : price;

    return (
        <div className="border border-border dark:border-dark-border bg-surface dark:bg-dark-surface p-4 rounded-md">
            <div className="aspect-square bg-background dark:bg-dark-background relative mb-2">
                {thumbnailUrl ? (
                    <Image
                        src={thumbnailUrl}
                        alt={name}
                        fill
                        className="object-cover rounded"
                    />
                ) : (
                    <div className="w-full h-full flex items-center justify-center text-sm text-text-secondary dark:text-dark-text-secondary">
                        이미지 없음
                    </div>
                )}
            </div>
            <h3 className="text-base font-semibold truncate">{name}</h3>
            <p className="text-sm text-text-secondary dark:text-dark-text-secondary line-through">
                {isTimeSale && timeSalePrice ? `${price.toLocaleString()}원` : ''}
            </p>
            <p className="text-lg font-bold text-primary">{displayPrice.toLocaleString()}원</p>
        </div>
    );
}
