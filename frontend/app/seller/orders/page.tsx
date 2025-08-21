// /app/seller/orders/page.tsx
import { listSellerOrders } from '@/lib/api/seller.server';

export const dynamic = 'force-dynamic';

export default async function SellerOrdersPage() {
    const res = await listSellerOrders({ page: 1, size: 20 });

    if (!res || res.items.length === 0) {
        return (
            <div className="border border-border dark:border-dark-border rounded-xl p-6 bg-surface dark:bg-dark-surface">
                <p className="text-text-secondary dark:text-dark-text-secondary">표시할 주문이 없습니다.</p>
            </div>
        );
    }

    return (
        <div className="border border-border dark:border-dark-border rounded-xl overflow-x-auto bg-surface dark:bg-dark-surface">
            <table className="w-full text-sm">
                <thead className="text-left">
                    <tr className="border-b border-border dark:border-dark-border">
                        <th className="px-4 py-3">주문번호</th>
                        <th className="px-4 py-3">상태</th>
                        <th className="px-4 py-3">결제금액</th>
                        <th className="px-4 py-3">주문일</th>
                    </tr>
                </thead>
                <tbody>
                    {res.items.map(o => (
                        <tr key={o.orderUid} className="border-t border-border/50 dark:border-dark-border/50">
                            <td className="px-4 py-3">{o.orderUid}</td>
                            <td className="px-4 py-3">{o.status}</td>
                            <td className="px-4 py-3">{o.payAmountFormatted ?? o.payAmount}</td>
                            <td className="px-4 py-3">{o.orderedAt}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
