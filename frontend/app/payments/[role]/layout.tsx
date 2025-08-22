// /app/payments/[role]/layout.tsx
import { cookies } from 'next/headers';
import { redirect, notFound } from 'next/navigation';

export default async function PaymentsRoleLayout({
    children,
    params,
}: { children: React.ReactNode; params: any }) {
    const { role } = params as { role: 'member' | 'guest' };
    if (role !== 'member' && role !== 'guest') notFound();

    const hasSession = Boolean((await cookies()).get('accessToken')?.value);
    if (role === 'member' && !hasSession) redirect('/signin?next=/payments/member/cart');
    if (role === 'guest' && hasSession) redirect('/payments/member/cart');

    return children;
}
