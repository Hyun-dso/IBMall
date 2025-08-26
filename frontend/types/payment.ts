// /types/payment.ts
export type PaymentMethod = 'CARD' | 'VBANK' | 'TRANSFER' | 'MOBILE';
export type PaymentStatus = 'PAID' | 'READY' | 'FAILED';

export interface PortOneResult {
    transactionId: string;
    pgProvider: string;        // 예: 'tosspay'
    paymentMethod: PaymentMethod;
    status: PaymentStatus;     // 보통 'PAID'
    paidAmount: number;        // 실제 승인 금액
}

export interface GuestSinglePaymentRequest {
    orderUid: string;
    productName: string;
    orderPrice: number;
    paidAmount: number;
    paymentMethod: PaymentMethod;
    status: PaymentStatus;
    transactionId: string;
    paymentId: string;
    pgProvider: string;
    productId: number;
    productOptionId?: number | null;
    quantity: number;
    buyerName: string;
    buyerEmail: string;
    buyerPhone: string;
    recipientName: string;
    recipientPhone: string;
    recipientAddress: string;
}

export interface GuestCartPaymentItem {
    productId: number;
    productOptionId: number;
    productName: string;
    quantity: number;
    price: number;
}

export interface GuestCartPaymentRequest {
    orderUid: string;
    productName: string; // "첫상품 외 N건"
    orderPrice: number;
    paidAmount: number;
    paymentMethod: PaymentMethod;
    status: PaymentStatus;
    transactionId: string;
    paymentId: string;
    pgProvider: string;
    buyerName: string;
    buyerEmail: string;
    buyerPhone: string;
    recipientName: string;
    recipientPhone: string;
    recipientAddress: string;
    items: GuestCartPaymentItem[];
}

export type PaymentRole = 'member' | 'guest';
export type PaymentFlow = 'single' | 'cart';

export interface OrderIntentPayload {
    intentId: string;
    productId: number;
    optionId?: number | null;
    quantity: number;
    unitPrice: number;
    currency: 'KRW';
    // 서버가 계산·봉인한 값들: 할인, 타임세일, 재고 스냅샷 등
    sealedAt: string;
    expiresAt: string;
}


export type CreateCartIntentInput = {
    role: PaymentRole;
    items: Array<{
        productId: number;
        productOptionId: number | null;
        quantity: number;
    }>;
};

export type VerifyIntentResponse = {
    intent: OrderIntentPayload;
    orderName: string;
    product: {
        productId: number;
        name: string;
        thumbnailUrl: string | null;
        optionName: string | null;
    };
};