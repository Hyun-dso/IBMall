// /app/signin/page.tsx
'use client';

import { useState } from 'react';
import Logo from '@/components/ui/Logo';
import Button from '@/components/ui/Button';
import { showToast } from '@/lib/toast';
import { signIn, getGoogleAuthUrl } from '@/lib/api/auth.client';
import { isSuccess } from '@/types/api'; // SUCCESS/FAIL 가드
import { INPUT_CLASS, INPUT_DIVIDER_CLASS } from '@/constants/styles';

export default function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [pending, setPending] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (pending) return;

        const trimmedEmail = email.trim();
        const trimmedPw = password.trim();
        if (!trimmedEmail || !trimmedPw) {
            showToast.error('이메일과 비밀번호를 모두 입력해야 해요', { group: 'auth.signin' });
            return;
        }

        try {
            setPending(true);
            showToast.loading('로그인 중...');

            // 이전: fetch Response 사용 → res.ok / res.json()
            // 변경: ApiResponse<Member> 사용 → code로 분기
            const result = await signIn({ email: trimmedEmail, password: trimmedPw });

            if (!isSuccess(result)) {
                showToast.error(result.message || '로그인에 실패했어요');
                return;
            }

            showToast.success('로그인에 성공했어요', { group: 'auth', persist: true, showNow: false });
            window.location.href = '/';
        } catch (err: unknown) {
            // http.ts 가 4xx/5xx에서 Error를 던짐
            const message = err instanceof Error ? err.message : '네트워크 오류가 발생했습니다.';
            showToast.error(message);
        } finally {
            setPending(false);
        }
    };

    const handleGoogleLogin = async () => {
        if (pending) return;
        try {
            setPending(true);
            showToast.loading('구글 로그인 준비 중...');

            // getGoogleAuthUrl 도 http.get<{url:string}>를 반환함 (Response 아님)
            const { url } = await getGoogleAuthUrl();
            if (!url) {
                showToast.error('Google OAuth URL을 받아오지 못했어요');
                return;
            }
            window.location.href = url;
        } catch (err: unknown) {
            const message = err instanceof Error ? err.message : 'Google OAuth 요청 중 오류 발생';
            showToast.error(message);
        } finally {
            setPending(false);
        }
    };

    return (
        <main className="min-h-screen flex items-center justify-center bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
            <form
                noValidate
                onSubmit={handleSubmit}
                className="w-full max-w-sm p-8 bg-surface dark:bg-dark-surface rounded-lg shadow-md"
            >
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
                        disabled={pending}
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
                        disabled={pending}
                        className={`${INPUT_CLASS}`}
                    />
                </div>

                <Button type="submit" full disabled={pending} className="font-bold">
                    {pending ? '처리 중...' : '로그인'}
                </Button>

                <Button
                    type="button"
                    onClick={handleGoogleLogin}
                    variant="outline"
                    full
                    disabled={pending}
                    className="mt-4 font-bold"
                >
                    Google 로그인
                </Button>
            </form>
        </main>
    );
}
