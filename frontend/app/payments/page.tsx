// /app/pay/page.tsx
import { redirect } from 'next/navigation';
import { getUserFromServer } from '@/lib/auth';
import { headers } from 'next/headers';

export default async function PayRouterPage({ searchParams }: { searchParams: Record<string, string> }) {
    const cookie = (await headers()).get('cookie') || '';
    const user = await getUserFromServer(cookie);

    const productId = searchParams.productId;

    if (!productId) {
        // 필수 파라미터 누락 시 에러 처리
        throw new Error('productId는 필수입니다.');
    }

    const redirectUrl = user
        ? `/payments/member?productId=${productId}`
        : `/payments/guest?productId=${productId}`;

    redirect(redirectUrl);
}
