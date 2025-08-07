export interface ProductLineItem {
    productId: number;
    name: string;
    price: number;
    thumbnailUrl: string | null;
    quantity: number;
    productOptionId?: number;
    optionName?: string;
    timeSalePrice?: number | null;
    isTimeSale?: boolean;
    disableQuantityControls?: boolean;
    showDeleteButton?: boolean;
}