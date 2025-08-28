// /components/Header.tsx
'use client';

import Link from 'next/link';
import Logo from './ui/Logo';
import type { MemberDto } from '@/types/account';

type Props = { user: MemberDto | null };

export default function Header({ user }: Props) {
    return (
        <header className="fixed top-0 w-full h-[var(--header-height)] border-b border-[var(--primary)] z-50 backdrop-blur bg-[var(--background)]">
            <div className="max-w-screen-xl mx-auto h-full flex items-center justify-between px-6">
                <Logo withText={true} size='lg' />
                <nav></nav>
                <div className='flex gap-2 items-center justify-center'>
                    {user ? (
                        <>
                            <Link href={'/mypage'} className="text-sm text-text-secondary dark:text-dark-text-secondary">
                                {user.role === 'SELLER' ? `판매자` : ''} {user.nickname}님
                            </Link>
                            <Link href={'/auth/signout'} className='p-2 rounded-lg border border-primary hover:cursor-pointer'>
                                로그아웃
                            </Link>
                        </>
                    ) : (
                        <>
                            <Link
                                href="/auth/signin"
                                aria-label="홈으로 이동"
                                className='p-2 w-24 justify-center text-[var(--white)] border rounded-lg flex hover:opecitiy-80'>로그인</Link>
                            <Link
                                href="/auth/signup"
                                aria-label="홈으로 이동"
                                className='p-2 w-24 justify-center text-[var(--primary)] border rounded-lg flex hover:opecitiy-80'>회원가입</Link>
                        </>
                    )}
                </div>
            </div>
        </header>
    );
}
