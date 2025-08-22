// /app/orders/complete/page.tsx
'use client';

import { useEffect, useMemo, useRef, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import Button from '@/components/ui/Button';

const CARD_CLASS =
    'border border-border dark:border-dark-border rounded-lg bg-surface dark:bg-dark-surface shadow-sm';
const currency = new Intl.NumberFormat('ko-KR');
const dtfmt = new Intl.DateTimeFormat('ko-KR', { dateStyle: 'medium', timeStyle: 'short' });
const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL;

type MemberOrderDetail = {
    orderUid: string;
    createdAt: string;
    status: string;
    totalPrice: number;
    items: { productId: number; productOptionId: number | null; productName: string; optionName: string | null; quantity: number; unitPrice: number }[];
    delivery?: { recipient: string; phone: string; address: string; trackingNumber: string; status: string } | null;
};

type GuestOrderDetail = {
    orderUid: string;
    createdAt: string;
    status: string;
    totalPrice: number;
    items: { productId: number; productOptionId: number | null; productName: string; optionName: string | null; quantity: number; unitPrice: number }[];
    delivery?: { recipientMasked: string; phoneMasked: string; addressMasked: string; trackingNumber: string; status: string } | null;
};

type ViewModel = {
    orderUid: string;
    createdAt: string;
    status: string;
    totalPrice: number;
    items: { name: string; optionName: string | null; quantity: number; unitPrice: number }[];
    delivery?: {
        recipient: string;
        phone: string;
        address: string;
        trackingNumber: string;
        status: string;
    } | null;
    source: 'member' | 'guest';
};

export default function OrderCompletePage() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const orderUidParam = searchParams.get('orderUid');
    const paymentIdParam = searchParams.get('paymentId'); // 결제 리디렉트에서 주로 포함됨

    const [loading, setLoading] = useState(true);
    const [vm, setVm] = useState<ViewModel | null>(null);

    const abortRef = useRef<AbortController | null>(null);

    useEffect(() => {
        abortRef.current?.abort();
        const ac = new AbortController();
        abortRef.current = ac;

        (async () => {
            setLoading(true);
            // 1) 회원 상세 우선 시도(파라미터 있으면 상세, 없으면 최신 1건)
            const vmMember = await tryFetchMemberDetail(orderUidParam, ac.signal);
            if (vmMember) {
                setVm(vmMember);
                setLoading(false);
                return;
            }

            // 2) 게스트 힌트로 조회
            const hintRaw = typeof window !== 'undefined' ? sessionStorage.getItem('guestOrderHint') : null;
            const hint = hintRaw ? safeParse(hintRaw) as { name?: string; phone?: string; orderUid?: string } : null;
            const vmGuest = hint?.name && hint?.phone
                ? await tryFetchGuestDetail(hint.name, normalizePhone(hint.phone), orderUidParam || hint.orderUid || null, ac.signal)
                : null;

            if (vmGuest) {
                setVm(vmGuest);
                setLoading(false);
                return;
            }

            // 3) 여기까지 실패했는데 쿼리 파라미터가 있었다면 기본값으로 복귀(쿼리 제거)
            if (orderUidParam || paymentIdParam) {
                router.replace('/orders/complete', { scroll: false });
                setLoading(false);
                return;
            }

            // 4) 기본 화면(상세 없음)
            setVm(null);
            setLoading(false);
        })();

        return () => ac.abort();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [orderUidParam, paymentIdParam]);

    const headerTitle = useMemo(() => {
        if (loading) return '주문을 확인하는 중...';
        return '주문이 완료되었습니다.';
    }, [loading]);

    return (
        <main className="min-h-screen px-4 py-8 bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
            <div className="mx-auto w-full max-w-3xl space-y-6">
                <section className={`p-6 ${CARD_CLASS}`}>
                    <h1 className="text-2xl font-bold mb-2">{headerTitle}</h1>
                    {paymentIdParam && (
                        <p className="text-sm text-text-secondary dark:text-dark-text-secondary">
                            결제 식별자: {paymentIdParam}
                        </p>
                    )}
                </section>

                {loading && (
                    <section className={`p-6 ${CARD_CLASS}`}>
                        <div className="animate-pulse space-y-3">
                            <div className="h-4 w-1/3 bg-border dark:bg-dark-border rounded" />
                            <div className="h-3 w-1/2 bg-border dark:bg-dark-border rounded" />
                            <div className="h-24 w-full bg-border dark:bg-dark-border rounded" />
                        </div>
                    </section>
                )}

                {!loading && vm && (
                    <>
                        <section className={`p-6 ${CARD_CLASS}`}>
                            <h2 className="text-lg font-semibold mb-2">주문 정보</h2>
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                                <InfoRow k="주문번호" v={vm.orderUid} />
                                <InfoRow k="주문일시" v={safeFormat(vm.createdAt)} />
                                <InfoRow k="주문상태" v={vm.status} />
                                <InfoRow k="결제금액" v={`${currency.format(vm.totalPrice)}원`} />
                                <InfoRow k="조회구분" v={vm.source === 'member' ? '회원' : '비회원(마스킹)'} />
                            </div>
                        </section>

                        <section className={`p-6 ${CARD_CLASS}`}>
                            <h2 className="text-lg font-semibold mb-3">주문 상품</h2>
                            <ul className="divide-y divide-border dark:divide-dark-border">
                                {vm.items.map((it, idx) => (
                                    <li key={idx} className="py-3 flex items-center justify-between">
                                        <div className="min-w-0">
                                            <div className="truncate">{it.name}</div>
                                            {it.optionName && (
                                                <div className="text-sm text-text-secondary dark:text-dark-text-secondary truncate">
                                                    {it.optionName}
                                                </div>
                                            )}
                                        </div>
                                        <div className="text-right shrink-0">
                                            <div className="text-sm text-text-secondary dark:text-dark-text-secondary">수량 {it.quantity}</div>
                                            <div className="font-semibold">{currency.format(it.unitPrice)}원</div>
                                        </div>
                                    </li>
                                ))}
                            </ul>
                        </section>

                        <section className={`p-6 ${CARD_CLASS}`}>
                            <h2 className="text-lg font-semibold mb-3">배송 정보</h2>
                            {vm.delivery ? (
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                                    <InfoRow k="수령인" v={vm.delivery.recipient} />
                                    <InfoRow k="연락처" v={vm.delivery.phone} />
                                    <InfoRow k="주소" v={vm.delivery.address} full />
                                    <InfoRow k="운송장" v={vm.delivery.trackingNumber} />
                                    <InfoRow k="배송상태" v={vm.delivery.status} />
                                </div>
                            ) : (
                                <div className="text-text-secondary dark:text-dark-text-secondary">배송 정보가 없습니다.</div>
                            )}
                        </section>
                    </>
                )}

                {!loading && !vm && (
                    <section className={`p-6 ${CARD_CLASS}`}>
                        <h2 className="text-lg font-semibold mb-2">주문 상세를 불러오지 못했습니다.</h2>
                        <p className="text-sm text-text-secondary dark:text-dark-text-secondary mb-4">
                            회원은 주문내역에서, 비회원은 주문조회에서 확인할 수 있습니다.
                        </p>
                        <div className="flex gap-2">
                            <Button className="flex-1" onClick={() => router.push('/')}>쇼핑 계속하기</Button>
                            <Button variant="outline" className="flex-1" onClick={() => router.push('/account/orders')}>주문 내역</Button>
                            <Button variant="outline" className="flex-1" onClick={() => router.push('/track/guest')}>비회원 주문조회</Button>
                        </div>
                    </section>
                )}
            </div>
        </main>
    );
}

/* 내부 컴포넌트 */
function InfoRow({ k, v, full }: { k: string; v: string; full?: boolean }) {
    return (
        <div className={full ? 'md:col-span-2' : ''}>
            <span className="mr-2 text-text-secondary dark:text-dark-text-secondary">{k}</span>
            <span className="font-medium">{v}</span>
        </div>
    );
}

/* 유틸 */
function safeParse(s: string) { try { return JSON.parse(s); } catch { return null; } }
function normalizePhone(p: string) { return String(p || '').replace(/[^0-9]/g, ''); }
function safeFormat(iso: string) { try { return dtfmt.format(new Date(iso)); } catch { return iso; } }

/* API 호출기 */
async function tryFetchMemberDetail(orderUidParam: string | null, signal: AbortSignal): Promise<ViewModel | null> {
    if (!API_BASE) return null;
    // 상세 먼저
    if (orderUidParam) {
        const d = await fetchJson<MemberOrderDetailResponse>(`${API_BASE}/api/member/orders/${encodeURIComponent(orderUidParam)}`, {
            method: 'GET',
            credentials: 'include',
            signal,
        });
        if (d?.success && d.data) return toVMFromMember(d.data);
    }
    // 최신 1건
    const q = new URLSearchParams({ page: '0', size: '1', status: 'ALL' }).toString();
    const list = await fetchJson<MemberOrderListResponse>(`${API_BASE}/api/member/orders?${q}`, {
        method: 'GET',
        credentials: 'include',
        signal,
    });
    const firstUid = list?.success ? list.data?.content?.[0]?.orderUid : null;
    if (!firstUid) return null;

    const d2 = await fetchJson<MemberOrderDetailResponse>(`${API_BASE}/api/member/orders/${encodeURIComponent(firstUid)}`, {
        method: 'GET',
        credentials: 'include',
        signal,
    });
    if (d2?.success && d2.data) return toVMFromMember(d2.data);
    return null;
}

async function tryFetchGuestDetail(name: string, phoneDigits: string, orderUidParam: string | null, signal: AbortSignal): Promise<ViewModel | null> {
    if (!API_BASE) return null;
    const headers = { 'Content-Type': 'application/json' };

    let orderUid = orderUidParam;
    if (!orderUid) {
        const body = { name: String(name).trim(), phone: String(phoneDigits), page: 0, size: 1 };
        const r = await fetchJson<GuestOrderListResponse>(`${API_BASE}/api/track/guest/orders/search`, {
            method: 'POST',
            credentials: 'include',
            headers,
            body: JSON.stringify(body),
            signal,
        });
        orderUid = r?.success ? r.data?.content?.[0]?.orderUid ?? null : null;
        if (!orderUid) return null;
    }

    const bodyDetail = { name: String(name).trim(), phone: String(phoneDigits) };
    const d = await fetchJson<GuestOrderDetailResponse>(`${API_BASE}/api/track/guest/orders/${encodeURIComponent(orderUid)}`, {
        method: 'POST',
        credentials: 'include',
        headers,
        body: JSON.stringify(bodyDetail),
        signal,
    });
    if (d?.success && d.data) return toVMFromGuest(d.data);
    return null;
}

async function fetchJson<T>(url: string, init: RequestInit): Promise<T | null> {
    try {
        const res = await fetch(url, init);
        if (!res.ok) return null;
        return (await res.json()) as T;
    } catch {
        return null;
    }
}

/* 타입: API 응답 래퍼 */
type SuccessWrap<T> = { success: true; data: T; message: string | null };
type MemberOrderListResponse = SuccessWrap<{
    content: { orderUid: string; createdAt: string; status: string; totalPrice: number; itemSummary: string; hasDelivery: boolean }[];
    page: number; size: number; totalElements: number; totalPages: number;
}>;
type MemberOrderDetailResponse = SuccessWrap<MemberOrderDetail>;

type GuestOrderListResponse = SuccessWrap<{
    content: { orderUid: string; createdAt: string; status: string; totalPrice: number; itemSummary: string; buyerNameMasked: string; buyerPhoneMasked: string; hasDelivery: boolean }[];
    page: number; size: number; totalElements: number; totalPages: number;
}>;
type GuestOrderDetailResponse = SuccessWrap<GuestOrderDetail>;

/* 뷰모델 변환 */
function toVMFromMember(d: MemberOrderDetail): ViewModel {
    return {
        orderUid: d.orderUid,
        createdAt: d.createdAt,
        status: d.status,
        totalPrice: d.totalPrice,
        items: d.items.map((i) => ({
            name: i.productName,
            optionName: i.optionName ?? null,
            quantity: i.quantity,
            unitPrice: i.unitPrice,
        })),
        delivery: d.delivery
            ? {
                recipient: d.delivery.recipient,
                phone: d.delivery.phone,
                address: d.delivery.address,
                trackingNumber: d.delivery.trackingNumber,
                status: d.delivery.status,
            }
            : null,
        source: 'member',
    };
}

function toVMFromGuest(d: GuestOrderDetail): ViewModel {
    return {
        orderUid: d.orderUid,
        createdAt: d.createdAt,
        status: d.status,
        totalPrice: d.totalPrice,
        items: d.items.map((i) => ({
            name: i.productName,
            optionName: i.optionName ?? null,
            quantity: i.quantity,
            unitPrice: i.unitPrice,
        })),
        delivery: d.delivery
            ? {
                recipient: d.delivery.recipientMasked,
                phone: d.delivery.phoneMasked,
                address: d.delivery.addressMasked,
                trackingNumber: d.delivery.trackingNumber,
                status: d.delivery.status,
            }
            : null,
        source: 'guest',
    };
}
