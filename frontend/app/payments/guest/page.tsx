// /app/payments/guest/page.tsx
import { redirect } from 'next/navigation';
import { cookies } from 'next/headers';

export default async function GuestPaymentsIndex() {
    const hasSession = Boolean((await cookies()).get('accessToken')?.value);
    if (hasSession) redirect('/payments/member/cart');
    redirect('/payments/guest/cart');
}
