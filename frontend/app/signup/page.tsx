// /app/signup/page.tsx
'use client';

import { useRef, useState } from 'react';
import Logo from '@/components/ui/Logo';
import Button from '@/components/ui/Button';
import { useRouter } from 'next/navigation';
import type { SignupForm } from '@/types/forms';
import { validate, validateField } from '@/lib/validators/engine';
import { normalizePhone } from '@/lib/validators/rules';
import { showToast } from '@/lib/toast';
import {
    FORM_CLASS,
    INPUT_CLASS,
    INPUT_GROUP_CLASS,
    INPUT_DIVIDER_CLASS,
    SR_ONLY,
    CENTER_CONTENT
} from '@/constants/styles';


export default function SignupPage() {
    const router = useRouter();

    const [form, setForm] = useState<SignupForm>({
        email: '',
        password: '',
        confirmPassword: '',
        name: '',
        nickname: '',
        phone: '',
        address1: '',
        address2: '',
    });
    const [submitting, setSubmitting] = useState(false);

    // 카카오 주소 찾기
    const detailRef = useRef<HTMLInputElement>(null);
    const [popupOpened, setPopupOpened] = useState(false);

    const setField = <K extends keyof SignupForm>(k: K, v: SignupForm[K]) =>
        setForm(prev => ({ ...prev, [k]: v }));

    const onChangePhone = (v: string) => setField('phone', normalizePhone(v));

    const hasText = (s?: string) => !!s && s.trim().length > 0;
    const onBlurConfirm = () => {
        if (!hasText(form.password) || !hasText(form.confirmPassword)) return;
        const msg = validateField('signup', 'confirmPassword', form);
        if (msg) {
            showToast.error('비밀번호 확인이 일치하지 않아요', { group: 'auth.signup' });
        }
    };

    const ensurePostcodeScript = async () => {
        if ((window as any).daum?.Postcode) return;
        await new Promise<void>((resolve) => {
            const script = document.createElement('script');
            script.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
            script.onload = () => resolve();
            document.body.appendChild(script);
        });
    };

    const handleSearchAddress = async () => {
        await ensurePostcodeScript();
        if (popupOpened) return;
        setPopupOpened(true);
        new (window as any).daum.Postcode({
            oncomplete: (data: any) => {
                setField('address1', data.address as string);
                setPopupOpened(false);
                setTimeout(() => detailRef.current?.focus(), 0);
            },
            onclose: () => setPopupOpened(false),
        }).open();
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const { ok, errors } = validate('signup', form);
        if (!ok) {
            const first = Object.values(errors)[0];
            if (first) showToast.error(first, { group: 'auth.signup' });
            return;
        }

        try {
            setSubmitting(true);
            showToast.loading('회원가입 중...', { group: 'auth.signup' });

            const res = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/members/signup`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    email: form.email,
                    password: form.password,
                    confirmPassword: form.confirmPassword,
                    name: form.name,
                    nickname: form.nickname,
                    phone: form.phone,
                    address: `${form.address1} ${form.address2}`.trim(),
                }),
                credentials: 'include',
            });

            if (!res.ok) {
                // 실패 메시지는 서버 표준에 맞춰 사용
                const data = await res.json().catch(() => null);
                showToast.error('회원가입에 실패했어요.');
                return;
            }

            showToast.success('회원가입 성공', { group: 'auth.signup', persist: true, showNow: false });
            router.push('/signin');
        } catch {
            showToast.error('네트워크 오류가 발생했습니다.', { group: 'auth.signup' });
        } finally {
            setSubmitting(false);
        }
    };

    return (<form noValidate onSubmit={handleSubmit} className={`${CENTER_CONTENT} ${FORM_CLASS} text-text-primary dark:text-dark-text-primary`}>
        <div className="w-full flex items-center justify-center">
            <Logo />
        </div>
        <h1 className="text-2xl font-bold mb-6 text-center">회원가입</h1>

        {/* 이메일 + 비밀번호 */}
        <div className={`mb-6 ${INPUT_GROUP_CLASS}`}>
            <label htmlFor="email" className={SR_ONLY}>이메일</label>
            <input
                id="email"
                type="email"
                placeholder="이메일"
                required
                value={form.email}
                onChange={(e) => setField('email', e.target.value)}
                className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                autoComplete="email"
                disabled={submitting}
            />
            <label htmlFor="password" className={SR_ONLY}>비밀번호</label>
            <input
                id="password"
                type="password"
                placeholder="비밀번호"
                required
                value={form.password}
                onChange={(e) => setField('password', e.target.value)}
                className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                autoComplete="new-password"
                disabled={submitting}
            />
            <label htmlFor="confirmPassword" className={SR_ONLY}>비밀번호 확인</label>
            <input
                id="confirmPassword"
                type="password"
                placeholder="비밀번호 확인"
                required
                value={form.confirmPassword}
                onChange={(e) => setField('confirmPassword', e.target.value)}
                onBlur={onBlurConfirm}
                className={INPUT_CLASS}
                autoComplete="new-password"
                disabled={submitting}
            />
        </div>

        {/* 이름 + 닉네임 */}
        <div className={`mb-4 ${INPUT_GROUP_CLASS}`}>
            <label htmlFor="name" className={SR_ONLY}>이름</label>
            <input
                id="name"
                type="text"
                placeholder="이름"
                required
                value={form.name}
                onChange={(e) => setField('name', e.target.value)}
                className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                autoComplete="name"
                disabled={submitting}
            />
            <label htmlFor="nickname" className={SR_ONLY}>닉네임</label>
            <input
                id="nickname"
                type="text"
                placeholder="닉네임"
                required
                value={form.nickname}
                onChange={(e) => setField('nickname', e.target.value)}
                className={INPUT_CLASS}
                autoComplete="nickname"
                disabled={submitting}
            />
        </div>

        {/* 연락처 + 주소 (선택 입력) */}
        <div className={`mb-6 ${INPUT_GROUP_CLASS}`}>
            <label htmlFor="phone" className={SR_ONLY}>연락처</label>
            <input
                id="phone"
                type="text"
                placeholder="연락처(선택)"
                value={form.phone}
                onChange={(e) => onChangePhone(e.target.value)}
                className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                autoComplete="tel"
                disabled={submitting}
            />
            <label htmlFor="address1" className={SR_ONLY}>주소</label>
            <input
                id="address1"
                type="text"
                placeholder="주소(선택)"
                value={form.address1}
                readOnly
                onClick={handleSearchAddress}
                onFocus={handleSearchAddress}
                className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS} cursor-pointer`}
                disabled={submitting}
            />
            <label htmlFor="address2" className={SR_ONLY}>상세주소</label>
            <input
                id="address2"
                type="text"
                placeholder="상세주소(선택)"
                value={form.address2}
                ref={detailRef}
                onChange={(e) => setField('address2', e.target.value)}
                className={INPUT_CLASS}
                disabled={submitting}
            />
        </div>

        <Button type="submit" full disabled={submitting} className="font-bold">
            {submitting ? '처리 중...' : '회원가입'}
        </Button>
    </form>
    );
}