// /app/api/payments/guest/single/route.ts
import { NextRequest, NextResponse } from 'next/server';

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL;

type IncomingBody = {
    orderUid: string;
    paymentId: string;
    intentId?: string | null;

    // 호환/표시 필드
    productId: number;
    productOptionId?: number | null;
    productName?: string | null;
    orderPrice?: number | null;
    paidAmount: number;
    quantity: number;

    // 구매자/수령자
    buyerName: string;
    buyerEmail?: string | null;
    buyerPhone: string;
    recipientName: string;
    recipientPhone: string;
    recipientAddress: string;
};

export async function POST(req: NextRequest) {
    try {
        if (!API_BASE) {
            return NextResponse.json(
                { message: 'API base URL이 설정되지 않았습니다.' },
                { status: 500 }
            );
        }

        const raw = (await req.json().catch(() => null)) as IncomingBody | null;
        if (!raw) {
            return NextResponse.json({ message: '잘못된 요청 본문' }, { status: 400 });
        }

        // 필수값 검증 (빈 문자열/0 방지)
        const missing: string[] = [];
        const required = [
            'orderUid',
            'paymentId',
            'productId',
            'quantity',
            'paidAmount',
            'buyerName',
            'buyerPhone',
            'recipientName',
            'recipientPhone',
            'recipientAddress',
        ] as const;

        for (const k of required) {
            const v = (raw as any)[k];
            if (
                v === undefined ||
                v === null ||
                (typeof v === 'string' && v.trim().length === 0) ||
                (typeof v === 'number' && Number.isNaN(v))
            ) {
                missing.push(k);
            }
        }
        if (missing.length) {
            return NextResponse.json(
                { message: `필수값 누락: ${missing.join(', ')}` },
                { status: 400 }
            );
        }

        // 정규화
        const payload = {
            // 결제 식별/멱등 키
            orderUid: String(raw.orderUid).trim(),
            paymentId: String(raw.paymentId).trim(),
            transactionId: String(raw.paymentId).trim(), // txId 없으면 paymentId 재사용
            intentId: raw.intentId ? String(raw.intentId).trim() : undefined,

            // 합계
            paidAmount: Number(raw.paidAmount ?? 0),
            orderPrice: raw.orderPrice != null ? Number(raw.orderPrice) : Number(raw.paidAmount ?? 0),

            // 상품(서버가 재검증하므로 표시/호환용)
            productId: Number(raw.productId),
            productOptionId:
                raw.productOptionId === undefined || raw.productOptionId === null
                    ? null
                    : Number(raw.productOptionId),
            productName: raw.productName ? String(raw.productName).trim() : undefined,
            quantity: Number(raw.quantity),

            // 구매자/수령자
            buyerName: String(raw.buyerName).trim(),
            buyerEmail:
                raw.buyerEmail && raw.buyerEmail.trim().length > 0
                    ? String(raw.buyerEmail).trim()
                    : null,
            buyerPhone: String(raw.buyerPhone).trim(),
            recipientName: String(raw.recipientName).trim(),
            recipientPhone: String(raw.recipientPhone).trim(),
            recipientAddress: String(raw.recipientAddress).trim(),

            // 프론트 참고값(서버에서 PG값으로 덮어씀)
            paymentMethod: 'CARD',
            status: 'PAID',
            pgProvider: 'INICIS',
        };

        // undefined 제거(백엔드에 깔끔하게 전달)
        const clean = JSON.parse(JSON.stringify(payload));

        const upstream = await fetch(`${API_BASE}/api/payments/guest/single`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            // 게스트라 쿠키 필요 없으면 omit 가능. 필요 시 req.headers.get('cookie') 전달.
            credentials: 'omit',
            body: JSON.stringify(clean),
        });

        const text = await upstream.text();
        // 백엔드의 상태코드/본문 그대로 전달
        return new NextResponse(text, {
            status: upstream.status,
            headers: { 'Content-Type': upstream.headers.get('Content-Type') || 'application/json' },
        });
    } catch (e: any) {
        return NextResponse.json(
            { message: e?.message || '프록시 처리 중 오류' },
            { status: 500 }
        );
    }
}
