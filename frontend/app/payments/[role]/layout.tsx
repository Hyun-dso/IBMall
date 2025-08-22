// /app/payments/[role]/layout.tsx
import { cookies } from 'next/headers';
import { redirect, notFound } from 'next/navigation';
import type { PaymentRole } from '@/types/payment';

type Params = { role: PaymentRole };

export default async function PaymentsRoleLayout({
  children,
  params,
}: {
  children: React.ReactNode;
  params: Promise<Params>; // ✅ Promise로 선언
}) {
  const { role } = await params;        // ✅ await로 추출

  if (role !== 'member' && role !== 'guest') notFound();

  // ✅ Next 15 기준 cookies()는 동기 사용
  const cookieStore = cookies();
  const hasSession = Boolean(cookieStore.get('accessToken')?.value);

  if (role === 'member' && !hasSession) redirect('/signin?next=/payments/member/cart');
  if (role === 'guest' && hasSession) redirect('/payments/member/cart');

  return <>{children}</>;
}
