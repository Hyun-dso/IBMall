// /components/mypage/MyPageProfile.tsx
'use client';

import { useEffect, useState } from 'react';
import AccountForm from '@/components/form/AccountForm';
import type { MemberDto, UpdateAccountRequest } from '@/types/account';
import { isSuccess } from '@/types/api';
import { showToast } from '@/lib/toast';


export default function MyPageProfile() {
    const [initial, setInitial] = useState<MemberDto | null>(null);
    const [pending, setPending] = useState(false);
    const group = 'auth.update';

    // 프로필 로드
    useEffect(() => {
        let cancelled = false;
        (async () => {
            try {
                const res = await fetch('/api/mypage/profile', {
                    credentials: 'include',
                    cache: 'no-store',
                });
                if (!res.ok) throw new Error('load');
                const json = await res.json();
                if (!cancelled) {
                    setInitial(isSuccess(json) ? (json.data as MemberDto) : null);
                }
            } catch {
                if (!cancelled) setInitial(null);
            }
        })();
        return () => {
            cancelled = true;
        };
    }, []);

    const handleSubmit = async (payload: UpdateAccountRequest) => {
        if (pending) return;
        setPending(true);
        try {
            showToast.loading('회원 정보를 수정 중이에요', { group });
            const res = await fetch('/api/mypage/profile', {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(payload),
            });

            let json = null;
            try { json = await res.json(); } catch { }

            if (!res.ok) {
                const msg = '회원 정보 수정에 실패했어요';
                showToast.error(msg, { group });
                return;
            }

            showToast.success('회원 정보를 수정했어요', { group, persist: true });
            setInitial(json.data as MemberDto);

            // SSR 의존 UI 갱신 필요 시
            window.location.reload();
        } catch {
            showToast.error('수정에 실패했어요', { group });
        } finally {
            setPending(false);
        }
    };

    return (
        <AccountForm
            key={initial ? 'loaded' : 'empty'}
            title="계정 수정"
            initial={initial ?? undefined}
            noEmail={true}
            noPhone={false}
            noAddress={false}
            noPassword={true}
            noName={false}
            noNickname={false}
            submitLabel={'수정'}
            onSubmit={handleSubmit}
        />
    );
}
