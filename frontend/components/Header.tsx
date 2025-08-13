// /components/Header.tsx
'use client';

import Link from 'next/link';
import Button from '@/components/ui/Button';
import type { User } from '@/types/auth';
import { showToast } from '@/lib/toast';
import Logo from './ui/Logo';
import { signout } from '@/lib/api/auth.client';
import { isSuccess } from '@/types/api';

interface Props { user: User | null; }

export default function Header({ user }: Props) {

    const handleLogout = async () => {
        try {
            const res = await signout();
            if (!isSuccess(res)) {
                showToast.error(res.message || '로그아웃에 실패했어요');
                return;
            }
            showToast.success('로그아웃에 성공했어요', { persist: true, showNow: false });
            window.location.assign('/');
        } catch (e) {
            const msg = e instanceof Error ? e.message : '로그아웃 중 오류가 발생했습니다.';
            showToast.error(msg);
        }
    };

    return (
        <header className="fixed top-0 left-0 w-full h-24 border-b border-primary z-50 backdrop-blur bg-white/25 dark:bg-dark-surface/25">
            <div className="max-w-screen-xl mx-auto h-full flex items-center justify-between px-6">
                <nav className="flex gap-4 items-center">
                    <Logo withText={true} />
                    {user ? (
                        <>
                            <span className="text-sm text-text-secondary dark:text-dark-text-secondary">
                                {user.nickname}님
                            </span>
                            <Button size="sm" onClick={handleLogout}>로그아웃</Button>
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
