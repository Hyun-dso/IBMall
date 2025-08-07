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
    user: User | null | undefined;
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

    return (
        <header className="fixed top-0 left-0 w-full h-24 border-b border-primary z-50 backdrop-blur bg-white/70 dark:bg-dark-surface">
            <div className="max-w-screen-xl mx-auto h-full flex items-center justify-between px-6">
                <Logo />
                <nav className="flex gap-4 items-center">
                    {typeof user === 'undefined' ? null : user ? (
                        <>
                            <span className="text-sm text-text-secondary dark:text-dark-text-secondary">
                                {user.nickname}님
                            </span>
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
