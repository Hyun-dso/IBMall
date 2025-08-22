// /app/mypage/page.tsx
import { redirect } from 'next/navigation';
import type { User } from '@/types/auth';
import MyPageProfile from '@/components/mypage/MyPageProfile';
import { headers } from 'next/headers';

export default async function MyPage() {
    const h = await headers();
    const cookie = h.get('cookie') || '';
    if (!cookie || !/accessToken=/.test(cookie)) redirect('/signin');

    const res = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/mypage/profile`, {
        headers: { cookie },
        cache: 'no-store',
    });
    if (res.status === 401) redirect('/signin');

    const json = await res.json();
    const profile: User = json?.data;

    return <MyPageProfile initialMember={profile} />;
}
