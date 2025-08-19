// /app/cart/page.tsx
import { cookies } from 'next/headers';
import CartPageClient from '@/components/cart/CartPageClient';
import type { ServerCartPayload, CartItem } from '@/types/cart';

const MIN_DESKTOP_WIDTH = 1024;

async function getMemberCartOrNull(): Promise<CartItem[] | null> {
    // 서버 쿠키 기반 회원 여부 판정
    const cookieStore = await cookies();
    const access = cookieStore.get('accessToken')?.value;
    if (!access) return null;

    // API 스펙 확인 필요: 회원 장바구니 조회 엔드포인트
    // 예시:
    const res = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/cart`, {
      method: 'GET',
      credentials: 'include',
      headers: { Cookie: `accessToken=${access}` },
      cache: 'no-store',
    });
    if (!res.ok) return null;
    const data: ServerCartPayload = await res.json();
    return data.items;

    return null;
}

export default async function CartPage() {
    const memberItems = await getMemberCartOrNull();

    return (
        <main className="min-h-screen bg-background text-text-primary dark:bg-dark-background dark:text-dark-text-primary">
            <section className="mx-auto w-full max-w-5xl px-4 py-8">
                <h1 className="text-2xl font-semibold mb-6">장바구니</h1>

                {/* 회원: SSR 전달, 비회원: 클라이언트 zustand에서 로드 */}
                <CartPageClient ssrItems={memberItems ?? undefined}  />
            </section>
        </main>
    );
}
