import { Phone } from "./common";

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

export interface CartPaymentItem {
    productId: number;
    productOptionId: number | null;
    productName: string;
    quantity: number;
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
    items: CartPaymentItem[];
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


export interface GuestPaymentForm {
    buyerName: string;
    buyerEmail: string;
    buyerPhone: string;
    address1: string;
    address2: string;
    sendToOther: boolean;
    recipientName: string;
    recipientPhone: string;
}

export type Payment1Request = {
    orderUid: string;
    productName: string; // "첫상품 외 N건"
    orderPrice: number;
    paidAmount: number;
    paymentMethod: PaymentMethod;
    status: PaymentStatus;
    transactionId: string;
    paymentId: string;
    pgProvider: string;
    items: CartPaymentItem[];

    name: string,
    email: string,
    phone: Phone,
    address1: string,
    address2: string,
    sendToOther: boolean,
    recipientName: string,
    recipientPhone: string,
}

// /types/payment.ts

// 폼이 onSubmit으로 넘겨줄 값 (게스트 기준, 회원도 비슷)
export type GuestCheckoutInput = {
    name: string;
    email: string;
    phone: string;
    address1: string;
    address2: string;
    sendToOther: boolean;
    recipientName: string;
    recipientPhone: string;
};

// 장바구니 라인아이템 (서버로 보낼 형태)
export type CartLineItemDto = {
    productId: number;
    quantity: number;
    productOptionId?: number; // 없으면 필드 자체 생략
};

// 결제 공통 DTO (서버 계약)
export type PaymentCommonDto = {
    orderUid: string;
    transactionId: string;
    paidAmount: number;
    orderPrice: number;
    paymentId: string;
    paymentMethod: 'CARD';
    status: 'PAID' | 'PENDING' | 'FAILED' | 'CANCELED';
    pgProvider: string;
    items: CartLineItemDto[];
};

// 게스트 결제 생성 DTO (서버 계약)
export type GuestPaymentCreateDto = PaymentCommonDto & {
    productId: number;
    productName: string;
    buyerName: string;
    buyerEmail: string | null;
    buyerPhone: string;
    recipientName: string;
    recipientPhone: string;
    recipientAddress1: string;
    recipientAddress2: string;
};

// 회원 결제 생성 DTO (서버 계약)
export type MemberPaymentCreateDto = PaymentCommonDto & {
    productName: string;
    originalAmount: number;
    recipientName: string;
    recipientPhone: string;
    recipientAddress1: string;
    recipientAddress2: string;
};


// /types/payment.ts
export type GuestSinglePaymentCreateDto = {
    // 멱등/결제 식별
    orderUid: string;
    transactionId: string;
    paymentId: string;

    // 금액
    paidAmount: number;
    orderPrice: number;

    // PG/상태
    paymentMethod: 'CARD';
    status: 'PAID' | 'PENDING' | 'FAILED' | 'CANCELED';
    pgProvider: string;

    // 상품 (단일)
    productId: number;
    productOptionId?: number;   // 옵션 없으면 필드 자체 생략
    productName: string;
    quantity: number;

    // 구매자/수령자
    buyerName: string;
    buyerEmail: string | null;
    buyerPhone: string;
    recipientName: string;
    recipientPhone: string;
    recipientAddress: string;   // ✅ address1 + address2 합친 단일 문자열
};

export type MemberSinglePaymentCreateDto = Omit<
    GuestSinglePaymentCreateDto,
    'buyerName' | 'buyerEmail' | 'buyerPhone'
> & {
    originalAmount: number;
};
