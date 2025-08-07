// /app/signin/page.tsx
'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Logo from '@/components/ui/Logo';
import Button from '@/components/ui/Button';
import { Toaster, toast } from 'react-hot-toast';

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
        toast.dismiss();

        if (!email.trim() || !password.trim()) {
            toast.error('이메일과 비밀번호를 모두 입력하세요.');
            return;
        }

        try {
            toast.loading('로그인 중...');

            const res = await fetch(`/api/auth/signin`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
                credentials: 'include',
            });

            toast.dismiss();

            if (!res.ok) {
                const { message } = await res.json();
                toast.error(message || '로그인에 실패했습니다.');
                return;
            }

            toast.success('로그인 성공');
            window.location.href = '/';
        } catch {
            toast.dismiss();
            toast.error('네트워크 오류가 발생했습니다.');
        }
    };

    const handleGoogleLogin = async () => {
        toast.dismiss();
        toast.loading('구글 로그인 준비 중...');

        try {
            const res = await fetch(`/api/oauth2/authorize/google`);
            toast.dismiss();

            if (!res.ok) {
                const { message } = await res.json();
                toast.error(message || '구글 로그인 실패');
                return;
            }

            const { url } = await res.json();
            if (!url) {
                toast.error('Google OAuth URL을 받아오지 못했습니다.');
                return;
            }

            window.location.href = url;
        } catch {
            toast.dismiss();
            toast.error('Google OAuth 요청 중 오류 발생');
        }
    };

    return (
        <main className="min-h-screen flex items-center justify-center bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
            <Toaster />
            <form
                noValidate
                onSubmit={handleSubmit}
                className="w-full max-w-sm p-8 bg-surface dark:bg-dark-surface rounded-lg shadow-md"
            >
                <div className="w-100 flex items-center justify-center">
                    <Logo />
                </div>
                <h1 className="text-2xl font-bold mb-6 text-center">로그인</h1>

                <div className="mb-6 border border-border dark:border-dark-border rounded-md overflow-hidden bg-surface dark:bg-dark-surface">
                    <input
                        id="email"
                        type="email"
                        placeholder="이메일"
                        required
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none"
                    />
                    <input
                        id="password"
                        type="password"
                        placeholder="비밀번호"
                        required
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="w-full px-4 py-3 bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none"
                    />
                </div>

                <Button
                    type="submit"
                    fullWidth
                    className="font-bold"
                >
                    로그인
                </Button>

                <Button
                    type="button"
                    onClick={handleGoogleLogin}
                    variant="outline"
                    fullWidth
                    className="mt-4 font-bold"
                >
                    Google 로그인
                </Button>
            </form>
        </main>
    );
}
