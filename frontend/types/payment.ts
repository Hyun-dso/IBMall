// /types/payment.ts

// 공통 결제 요청 필드
interface BaseGuestPaymentRequest {
    orderUid: string;
    productName: string;
    orderPrice: number;
    paidAmount: number;
    paymentMethod: 'CARD' | 'VIRTUAL_ACCOUNT' | 'KAKAOPAY' | string;
    status: 'PAID' | 'FAILED' | 'CANCELLED' | string;
    transactionId: string;
    pgProvider: string;
    buyerName: string;
    buyerEmail: string;
    buyerPhone: string;
    buyerAddress: string;
    recipientName: string;
    recipientPhone: string;
    recipientAddress: string;
}

// 단일 상품 결제
export interface GuestSinglePaymentRequest extends BaseGuestPaymentRequest {
    productId: number;
    productOptionId: number;
    quantity: number;
}

// 장바구니 결제
export interface GuestCartPaymentItem {
    productId: number;
    productOptionId: number;
    productName: string;
    quantity: number;
    price: number;
}

export interface GuestCartPaymentRequest extends BaseGuestPaymentRequest {
    items: GuestCartPaymentItem[];
}

// 공통 응답
export interface GuestPaymentResponse {
    code: 'SUCCESS' | 'FAILURE' | string;
    message: string;
    data: unknown | null;
}
