// /components/account/UnifiedAccountForm.tsx
'use client';

import { useMemo, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { showToast } from '@/lib/toast';
import {
    CENTER_CONTENT, FORM_CLASS, INPUT_GROUP_CLASS, INPUT_CLASS, INPUT_DIVIDER_CLASS
} from '@/app/constants/styles';
import Logo from '@/components/ui/Logo';

type Props = { next?: string }; // ← props로도 받을 수 있게

function sanitizePath(p: string): string {
    // 절대 URL/프로토콜/이중 슬래시 차단 → 내부 경로만 허용
    if (!p) return '/';
    if (p.startsWith('http://') || p.startsWith('https://')) return '/';
    if (!p.startsWith('/') || p.startsWith('//')) return '/';
    return p;
}

export default function Form({ next }: Props) {
    const router = useRouter();
    const sp = useSearchParams();

    // 1) props.next 우선  2) ?next=쿼리  3) 기본값 '/'
    const rawNext = next ?? sp.get('next') ?? '/';
    const redirectTo = useMemo(() => sanitizePath(rawNext), [rawNext]);

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [pending, setPending] = useState(false);

    const group = 'auth.signin';

    const handleSignin = async (e: React.FormEvent) => {
        e.preventDefault();
        if (pending) return;

        if (!email.trim() || !password.trim()) {
            showToast.error('이메일과 비밀번호를 모두 입력해야 해요', { group });
            return;
        }

        try {
            setPending(true);
            showToast.loading('로그인 중...', { group });

            const res = await fetch('/api/auth/signin', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({ email, password }),
            });

            if (!res.ok) {
                showToast.error('로그인에 실패했어요', { group });
                setPending(false);
                return;
            }

            showToast.success('로그인에 성공했어요', { group, persist: true });
            if (next === (undefined)) window.location.href = '/'; else window.location.href = '/' + next;
        } catch {
            showToast.error('네트워크 오류가 발생했습니다.', { group });
            setPending(false);
        }
    };

    const handleGoogleLogin = async () => {
        if (pending) return;
        setPending(true);
        showToast.loading('구글 로그인 준비 중...', { group });

        try {
            const res = await fetch('/api/oauth2/authorize/google', { credentials: 'include' });
            if (!res.ok) throw new Error('oauth');
            const { url } = await res.json();
            if (!url) throw new Error('missing url');
            router.push(url);
        } catch {
            showToast.error('Google OAuth 요청 중 오류가 발생했어요', { group });
            setPending(false);
        }
    };

    return (
        <form onSubmit={handleSignin} className={`${FORM_CLASS} ${CENTER_CONTENT} flex flex-col justify-center items-center`}>
            <Logo size="lg" />
            <h2 className="text-lg font-semibold">계정 활동하기</h2>

            <div className={`mt-4 ${INPUT_GROUP_CLASS}`}>
                <input
                    className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                    type="email"
                    placeholder="이메일"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    autoComplete="email"
                    autoCapitalize="none"
                    autoCorrect="off"
                />
                <input
                    className={INPUT_CLASS}
                    type="password"
                    placeholder="비밀번호"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
            </div>

            <button type='submit' onClick={handleSignin} disabled={pending} className='w-full mt-4 p-2 rounded-md border border-[var(--primary)] hover:cursor-pointer disabled:cursor-not-allowed'>
                {pending ? '로그인 중...' : '로그인'}
            </button>
            <button type='submit' onClick={handleGoogleLogin} disabled={pending} className='w-full mt-2 p-2 rounded-md border border-[var(--border] hover:cursor-pointer disabled:cursor-not-allowed'>
                {pending ? '로그인 중...' : 'Google 로그인'}
            </button>
        </form>
    );
}
