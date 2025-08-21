// /app/seller/page.tsx
import { getSellerDashboard } from '@/lib/api/seller.server';

export const dynamic = 'force-dynamic';

export default async function SellerHomePage() {
  const data = await getSellerDashboard();

  if (!data) {
    return (
      <div className="border border-border dark:border-dark-border rounded-xl p-6 bg-surface dark:bg-dark-surface">
        <p className="text-text-secondary dark:text-dark-text-secondary">대시보드 데이터를 불러올 수 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {/* <StatCard title="오늘 매출" value={data.todaySalesFormatted ?? data.todaySales ?? '-'} />
      <StatCard title="이번달 매출" value={data.monthSalesFormatted ?? data.monthSales ?? '-'} />
      <StatCard title="주문 대기" value={data.pendingOrders ?? '-'} />
      <StatCard title="상품 수" value={data.productsCount ?? '-'} /> */}
    </div>
  );
}
