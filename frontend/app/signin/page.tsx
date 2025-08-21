// /app/signin/page.tsx
'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Logo from '@/components/ui/Logo';
import Button from '@/components/ui/Button';
import { INPUT_CLASS, INPUT_DIVIDER_CLASS, CENTER_CONTENT, FORM_CLASS } from '@/constants/styles';
import { showToast } from '@/lib/toast';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL;
if (!API_BASE_URL) {
    throw new Error('환경 변수 NEXT_PUBLIC_API_BASE_URL이 정의되지 않았습니다.');
}

export default function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const router = useRouter();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!email.trim() || !password.trim()) {
            showToast.error('이메일과 비밀번호를 모두 입력해야 해요', { group: 'auth.signin' });
            return;
        }

        try {
            showToast.loading('로그인 중...', { group: 'auth.signin' });

            const res = await fetch(`/api/auth/signin`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
                credentials: 'include',
            });

            if (!res.ok) {
                showToast.error('로그인에 실패했어요', { group: 'auth.signin' });
                return;
            }
            showToast.success('로그인에 성공했어요', { group: 'auth.signin', persist: true, showNow: false });
            window.location.href = '/';
        } catch {
            showToast.error('네트워크 오류가 발생했습니다.', { group: 'auth.signin' });
        }
    };

    const handleGoogleLogin = async () => {
        showToast.loading('구글 로그인 준비 중...', { group: 'auth.signin' });

        try {
            const res = await fetch(`/api/oauth2/authorize/google`);

            if (!res.ok) {
                const { message } = await res.json();
                showToast.error('구글 로그인 실패', { group: 'auth.signin' });
                return;
            }

            const { url } = await res.json();
            if (!url) {
                showToast.error('Google OAuth URL을 받아오지 못했습니다.');
                return;
            }

            window.location.href = url;
        } catch {
            showToast.error('Google OAuth 요청 중 오류 발생', { group: 'auth.signin' });
        }
    };

    return (
        <form noValidate onSubmit={handleSubmit} className={`${CENTER_CONTENT} ${FORM_CLASS} text-text-primary dark:text-dark-text-primary`}>
            <div className="w-full flex items-center justify-center">
                <Logo />
            </div>
            <h1 className="text-2xl font-bold mb-6 text-center">로그인</h1>

            <div className="mb-6 border border-border dark:border-dark-border rounded-md overflow-hidden bg-surface dark:bg-dark-surface">
                <label htmlFor="email" className="sr-only">이메일</label>
                <input
                    id="email"
                    type="email"
                    autoComplete="username"
                    placeholder="이메일"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                />
                <label htmlFor="password" className="sr-only">비밀번호</label>
                <input
                    id="password"
                    type="password"
                    autoComplete="current-password"
                    placeholder="비밀번호"
                    required
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className={INPUT_CLASS}
                />
            </div>

            <Button type="submit" full className="font-bold">
                로그인
            </Button>

            <Button
                type="button"
                onClick={handleGoogleLogin}
                variant="outline"
                full
                className="mt-4 font-bold"
            >
                Google 로그인
            </Button>
        </form>

    );
}
