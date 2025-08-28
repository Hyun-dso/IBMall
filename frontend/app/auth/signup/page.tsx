// /components/account/UnifiedAccountForm.tsx
'use client';

import AccountForm from '@/components/form/AccountForm';
import { showToast } from '@/lib/toast';
import type { UpdateAccountRequest } from '@/types/account';
import { useState } from 'react';

export default function AccountPage() {
    const [pending, setPending] = useState(false);
    const group = 'auth.signup';

    // AccountForm이 검증까지 끝낸 payload를 넘겨줌
    const handleSubmit = async (payload: UpdateAccountRequest) => {
        if (pending) return;
        setPending(true);
        try {
            showToast.loading('회원가입을 하는 중이에요', { group });
            const res = await fetch('/api/members/signup', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(payload),
            });

            if (!res.ok) {
                const msg = '회원가입에 실패했어요';
                showToast.error(msg, { group });
                return;
            }

            showToast.success('회원가입에 성공했어요', { group, persist: true });
            window.location.href = '/auth/signin';
        } catch {
            showToast.error('네트워크 오류가 발생했어요', { group });
        } finally {
            setPending(false);
        }
    };

    return (
        <AccountForm
            title="계정 입력"
            submitLabel={'생성'}
            onSubmit={handleSubmit}
        />
    );
}
