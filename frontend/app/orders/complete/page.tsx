// /app/track/order/page.tsx
'use client';

import { useEffect, useMemo, useRef, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { showToast } from '@/lib/toast';

const CARD_CLASS =
  'border border-border dark:border-dark-border rounded-lg bg-surface dark:bg-dark-surface shadow-sm';
const INPUT_CLASS =
  'w-full px-4 py-3 bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none';

const currency = new Intl.NumberFormat('ko-KR');
const dtfmt = new Intl.DateTimeFormat('ko-KR', { dateStyle: 'medium', timeStyle: 'short' });
const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL;

// ===== 타입 =====
type ApiResponse<T> = { code: string; message: string | null; data: T };

type GuestOrderListItem = {
  orderUid: string;
  createdAt: string;
  status: string;
  totalPrice: number;
  itemSummary: string;
  buyerNameMasked: string;
  buyerPhoneMasked: string;
  hasDelivery: boolean;
};
type GuestOrderListData = {
  content: GuestOrderListItem[];
  page: number; size: number; totalElements: number; totalPages: number;
};
type GuestOrderDetail = {
  orderUid: string;
  createdAt: string;
  status: string;
  totalPrice: number;
  items: {
    productId: number;
    productOptionId: number | null;
    productName: string;
    optionName: string | null;
    quantity: number;
    unitPrice: number;
  }[];
  delivery?: {
    recipientMasked: string;
    phoneMasked: string;
    addressMasked: string;
    trackingNumber: string;
    status: string;
  } | null;
};

type OrderDetailDto = {
  orderId: number;
  createdAt: string;
  orderStatus: string;
  buyerName: string;
  productName: string;
  productPrice: number;
  quantity: number;
  itemTotal: number;
  deliveryAddress1: string;
  deliveryAddress2: string;
  recipient: string;
  phone: string;
  trackingNumber: string;
  deliveryStatus: string;
};

// ===== 유틸 =====
function normalizePhone(p: string) {
  return String(p || '').replace(/[^0-9]/g, '');
}
function fmtDateISO(d: Date) {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${dd}`;
}
function todayISO() { return fmtDateISO(new Date()); }
function ninetyDaysAgoISO() { const d = new Date(); d.setDate(d.getDate() - 90); return fmtDateISO(d); }
function safeFormat(iso: string) { try { return dtfmt.format(new Date(iso)); } catch { return iso; } }
async function fetchJson<T>(url: string, init: RequestInit): Promise<T | null> {
  try {
    const res = await fetch(url, init);
    if (!res.ok) return null;
    return (await res.json()) as T;
  } catch { return null; }
}

// ===== 페이지 =====
export default function TrackOrderPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const qpOrderUid = searchParams.get('orderUid');

  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');

  const [from, setFrom] = useState(ninetyDaysAgoISO());
  const [to, setTo] = useState(todayISO());

  const [orderUid, setOrderUid] = useState(qpOrderUid ?? '');

  const [loading, setLoading] = useState(false);
  const [list, setList] = useState<GuestOrderListItem[] | null>(null);
  const [detail, setDetail] = useState<GuestOrderDetail | null>(null);

  const detailRef = useRef<HTMLDivElement | null>(null);

  // 쿼리 파라미터로 들어온 orderUid 자동 입력
  useEffect(() => { if (qpOrderUid) setOrderUid(qpOrderUid); }, [qpOrderUid]);

  const canSearch = useMemo(
    () => name.trim().length > 0 && normalizePhone(phone).length >= 7,
    [name, phone]
  );

  const onSearchList = async () => {
    if (!API_BASE) {
      showToast.error('API base URL이 설정되지 않았습니다.');
      return;
    }
    if (!canSearch) {
      showToast.error('이름과 전화번호를 입력하세요.');
      return;
    }
    setLoading(true);
    setDetail(null);
    setList(null);
    try {
      const body = {
        name: name.trim(),
        phone: normalizePhone(phone),
        from,
        to,
        page: 0,
        size: 10,
      };
      const r = await fetchJson<ApiResponse<GuestOrderListData>>(
        `${API_BASE}/api/track/guest/orders/search`,
        {
          method: 'POST',
          credentials: 'include',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(body),
        }
      );
      if (!(r && r.code === 'SUCCESS')) {
        showToast.error('주문 목록 조회에 실패했어요');
        return;
      }
      setList(r.data.content ?? []);
      if (!r.data.content || r.data.content.length === 0) {
        showToast.error('조회된 주문이 없어요');
      }
    } catch {
      showToast.error('주문 목록 조회 중 오류가 발생했어요');
    } finally {
      setLoading(false);
    }
  };

  const onFetchDetail = async (uid: string) => {
    if (!API_BASE) {
      showToast.error('API base URL이 설정되지 않았습니다.');
      return;
    }
    const nm = name.trim();
    const ph = normalizePhone(phone);
    if (!(nm && ph)) {
      showToast.error('이름과 전화번호를 입력해야 해요');
      return;
    }
    setLoading(true);
    setDetail(null);
    try {
      const r = await fetchJson<OrderDetailDto>(
        `${API_BASE}/api/order-detail/${encodeURIComponent(uid)}`,
        {
          method: 'GET',
          credentials: 'include',
        }
      );
      if (!r) {
        showToast.error('주문 상세 조회에 실패했어요');
        return;
      }
      const delivery = r.recipient
        ? {
            recipientMasked: r.recipient,
            phoneMasked: r.phone,
            addressMasked: `${r.deliveryAddress1} ${r.deliveryAddress2}`.trim(),
            trackingNumber: r.trackingNumber,
            status: r.deliveryStatus,
          }
        : null;
      const detail: GuestOrderDetail = {
        orderUid: uid,
        createdAt: r.createdAt,
        status: r.orderStatus,
        totalPrice: r.itemTotal,
        items: [
          {
            productId: 0,
            productOptionId: null,
            productName: r.productName,
            optionName: null,
            quantity: r.quantity,
            unitPrice: r.productPrice,
          },
        ],
        delivery,
      };
      setDetail(detail);
      // 완료 페이지 힌트 저장
      try {
        sessionStorage.setItem('guestOrderHint', JSON.stringify({ name: nm, phone: ph, orderUid: uid }));
      } catch {}
      // 스크롤 이동
      setTimeout(() => detailRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' }), 0);
    } catch {
      showToast.error('주문 상세 조회 중 오류가 발생했어요');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (orderUid.trim()) {
      await onFetchDetail(orderUid.trim());
    } else {
      await onSearchList();
    }
  };

  return (
    <main className="min-h-screen px-4 py-8 bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
      <div className="mx-auto w-full max-w-3xl space-y-6">
        <section className={`p-6 ${CARD_CLASS}`}>
          <h1 className="text-2xl font-bold mb-4">비회원 주문 조회</h1>
          <form onSubmit={onSubmit} noValidate>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mb-3">
              <input
                name="name"
                placeholder="이름"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className={`${INPUT_CLASS} border border-border dark:border-dark-border rounded-md`}
              />
              <input
                name="phone"
                placeholder="전화번호(숫자만)"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                className={`${INPUT_CLASS} border border-border dark:border-dark-border rounded-md`}
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-3 mb-3">
              <input
                name="from"
                type="date"
                value={from}
                max={to}
                onChange={(e) => setFrom(e.target.value)}
                className={`${INPUT_CLASS} border border-border dark:border-dark-border rounded-md`}
              />
              <input
                name="to"
                type="date"
                value={to}
                min={from}
                onChange={(e) => setTo(e.target.value)}
                className={`${INPUT_CLASS} border border-border dark:border-dark-border rounded-md`}
              />
              <input
                name="orderUid"
                placeholder="주문번호(선택)"
                value={orderUid}
                onChange={(e) => setOrderUid(e.target.value)}
                className={`${INPUT_CLASS} border border-border dark:border-dark-border rounded-md`}
              />
            </div>

            <div className="flex gap-2">
              <button type="submit" disabled={loading}>
                {orderUid.trim() ? '상세 조회' : '목록 검색'}
              </button>
              <button
                type="button"
                onClick={() => {
                  setOrderUid('');
                  setList(null);
                  setDetail(null);
                }}
              >
                초기화
              </button>
            </div>
          </form>
        </section>

        {/* 목록 */}
        {list && (
          <section className={`p-6 ${CARD_CLASS}`}>
            <h2 className="text-lg font-semibold mb-3">검색 결과</h2>
            {list.length === 0 ? (
              <div className="text-text-secondary dark:text-dark-text-secondary">결과가 없습니다.</div>
            ) : (
              <ul className="divide-y divide-border dark:divide-dark-border">
                {list.map((o) => (
                  <li key={o.orderUid} className="py-3 flex items-center justify-between">
                    <div className="min-w-0">
                      <div className="font-medium truncate">{o.itemSummary}</div>
                      <div className="text-sm text-text-secondary dark:text-dark-text-secondary">
                        {safeFormat(o.createdAt)} • {o.status} • {currency.format(o.totalPrice)}원 • {o.orderUid}
                      </div>
                      <div className="text-xs text-text-secondary dark:text-dark-text-secondary">
                        {o.buyerNameMasked} / {o.buyerPhoneMasked}
                      </div>
                    </div>
                    <div className="shrink-0">
                      <button onClick={() => onFetchDetail(o.orderUid)}>
                        상세
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </section>
        )}

        {/* 상세 */}
        {detail && (
          <section ref={detailRef} className={`p-6 ${CARD_CLASS}`}>
            <h2 className="text-lg font-semibold mb-3">주문 상세</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm mb-4">
              <InfoRow k="주문번호" v={detail.orderUid} />
              <InfoRow k="주문일시" v={safeFormat(detail.createdAt)} />
              <InfoRow k="주문상태" v={detail.status} />
              <InfoRow k="결제금액" v={`${currency.format(detail.totalPrice)}원`} />
            </div>

            <h3 className="text-base font-semibold mb-2">상품</h3>
            <ul className="divide-y divide-border dark:divide-dark-border mb-4">
              {detail.items.map((it, idx) => (
                <li key={idx} className="py-3 flex items-center justify-between">
                  <div className="min-w-0">
                    <div className="truncate">{it.productName}</div>
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

            <h3 className="text-base font-semibold mb-2">배송</h3>
            {detail.delivery ? (
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                <InfoRow k="수령인" v={detail.delivery.recipientMasked} />
                <InfoRow k="연락처" v={detail.delivery.phoneMasked} />
                <InfoRow k="주소" v={detail.delivery.addressMasked} full />
                <InfoRow k="운송장" v={detail.delivery.trackingNumber} />
                <InfoRow k="배송상태" v={detail.delivery.status} />
              </div>
            ) : (
              <div className="text-text-secondary dark:text-dark-text-secondary">배송 정보가 없습니다.</div>
            )}

            <div className="mt-6 flex gap-2">
              <button
                className="flex-1"
                onClick={() => {
                  const nm = name.trim();
                  const ph = normalizePhone(phone);
                  // 완료 페이지가 즉시 게스트 조회 가능하도록 name/phone도 함께 전달
                  const url = `/orders/complete?orderUid=${encodeURIComponent(detail.orderUid)}&name=${encodeURIComponent(nm)}&phone=${encodeURIComponent(ph)}`;
                  router.push(url);
                }}
              >
                주문 완료 화면으로
              </button>
              <button
                className="flex-1"
                onClick={() => {
                  setDetail(null);
                  try {
                    const nm = name.trim();
                    const ph = normalizePhone(phone);
                    if (nm && ph) {
                      sessionStorage.setItem('guestOrderHint', JSON.stringify({ name: nm, phone: ph, orderUid: detail.orderUid }));
                    }
                  } catch {}
                }}
              >
                세션 힌트 저장
              </button>
            </div>
          </section>
        )}

        {/* 로딩 */}
        {loading && (
          <section className={`p-6 ${CARD_CLASS}`}>
            <div className="animate-pulse space-y-3">
              <div className="h-4 w-1/3 bg-border dark:bg-dark-border rounded" />
              <div className="h-3 w-1/2 bg-border dark:bg-dark-border rounded" />
              <div className="h-24 w-full bg-border dark:bg-dark-border rounded" />
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
