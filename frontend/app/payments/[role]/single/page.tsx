// /app/payments/[role]/single/page.tsx
// 기본 분기는 cart로 통합했으므로, single은 의도(intent) 기반 진입만 허용.
// 쿼리 productId는 무시. intent 검증 실패 시 대체 UI.
import SinglePaymentPage from '@/components/payments/SinglePaymentPage';
export default function SinglePage({ params }: { params: { role: 'member' | 'guest' } }) {
    return <SinglePaymentPage role={params.role} />;
}
