// /app/payments/member/page.tsx
import { redirect } from 'next/navigation';
import { cookies } from 'next/headers';

export default async function MemberPaymentsIndex() {
  const hasSession = Boolean((await cookies()).get('accessToken')?.value);
  if (!hasSession) redirect('/signin?next=/payments/member/cart');
  redirect('/payments/member/cart');
}
