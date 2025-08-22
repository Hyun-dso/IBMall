// /lib/payments/client.ts
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;

import type { PaymentRole, CreateCartIntentInput, VerifyIntentResponse } from '@/types/payment';

// 엔드포인트는 확정 필요. 현재는 예시.
export async function createOrderIntentForCart(input: CreateCartIntentInput) {
    const res = await fetch(`${API_BASE_URL}/api/orders/intent/cart`, {
        method: 'POST',
        credentials: 'include',
        headers: { 'content-type': 'application/json' },
        body: JSON.stringify(input),
    });
    if (!res.ok) {
        let msg = '의도 생성 실패';
        try {
            const j = await res.json();
            msg = j.message || msg;
        } catch { }
        throw new Error(msg);
    }
    return res.json() as Promise<{ intentId: string }>;
}

export async function verifyOrderIntent(intentId: string, role: PaymentRole) {
    const res = await fetch(
        `${API_BASE_URL}/api/orders/intent/verify?intent=${encodeURIComponent(intentId)}&role=${role}`,
        { method: 'GET', credentials: 'include', cache: 'no-store' },
    );
    if (!res.ok) {
        let msg = '의도 검증 실패';
        try {
            const j = await res.json();
            msg = j.message || msg;
        } catch { }
        throw new Error(msg);
    }
    return res.json() as Promise<VerifyIntentResponse>;
}

export async function completeGuestSinglePayment(payload: {
    intentId: string;
    paymentId: string;
    orderUid: string;
    productId: number;
    productOptionId: number | null;
    quantity: number;
    orderPrice: number;
    paidAmount: number;
    buyerName: string;
    buyerEmail: string;
    buyerPhone: string;
    recipientName: string;
    recipientPhone: string;
    recipientAddress: string;
}) {
    const res = await fetch('/api/payments/guest/single', {
        method: 'POST',
        credentials: 'include',
        headers: { 'content-type': 'application/json' },
        body: JSON.stringify(payload),
    });
    if (!res.ok) return false;
    return true;
}
