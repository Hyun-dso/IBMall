// /components/Header.tsx
'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { toast } from 'react-hot-toast';
import Logo from '@/components/ui/Logo';
import Button from '@/components/ui/Button';
import type { User } from '@/types/auth';


const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;
interface Props {
    user: User | null;
}

export default function Header({ user }: Props) {
    const router = useRouter();

    const handleLogout = async () => {
        try {
            const res = await fetch(`/api/auth/signout`, {
                method: 'POST',
                credentials: 'include',
            });

            if (!res.ok) {
                const { message } = await res.json();
                toast.error(message || '로그아웃 실패');
                return;
            }

            toast.success('로그아웃되었습니다');
            router.refresh();
        } catch {
            toast.error('로그아웃 중 오류가 발생했습니다.');
        }
    };

    const nickname = user?.nickname || user?.name || (user?.email ? user.email.split('@')[0] : '사용자');
    const isSeller = user?.role === 'SELLER';
    const myHref = isSeller ? '/seller/dashboard' : '/mypage';

    return (
        <header className="fixed top-0 left-0 w-full h-[var(--header-height)] border-b border-primary z-50 backdrop-blur bg-white/25 dark:bg-dark-surface/25">
            <div className="max-w-screen-xl mx-auto h-full flex items-center justify-between px-6">
                <Logo withText={true} />
                <nav className="flex gap-4 items-center">
                    {user ? (
                        <>
                            <Link href={myHref} className="text-sm text-text-secondary dark:text-dark-text-secondary">
                                {isSeller ? `판매자 ${nickname}님` : `${nickname}님`}
                            </Link>
                            <Button size="sm" onClick={handleLogout}>
                                로그아웃
                            </Button>
                        </>
                    ) : (
                        <>
                            <Button asChild size="sm" variant="outline">
                                <Link href="/signin">로그인</Link>
                            </Button>
                            <Button asChild size="sm" variant="solid">
                                <Link href="/signup">회원가입</Link>
                            </Button>
                        </>
                    )}
                </nav>
            </div>
        </header>

    );
}
