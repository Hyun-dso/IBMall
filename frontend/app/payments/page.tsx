// /app/payments/page.tsx
import { redirect } from 'next/navigation';
import { headers } from 'next/headers';
import { getUserFromServer } from '@/lib/auth';

export default async function PaymentsIndexPage() {
  const cookie = (await headers()).get('cookie') || '';
  const user = await getUserFromServer(cookie);
  // 서버 쿠키 기반 회원 여부 판별
  if (user) redirect('/payments/member');
  redirect('/payments/guest');
}
