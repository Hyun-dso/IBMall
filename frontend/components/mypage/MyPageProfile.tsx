// /components/mypage/MyPageProfile.tsx
'use client';

import { useState } from 'react';
import type { User, ProfileUpdateRequest } from '@/types/auth';
import { showToast } from '@/lib/toast';
import Button from '@/components/ui/Button';
import {
    FORM_CLASS,
    INPUT_CLASS,
    INPUT_GROUP_CLASS,
    INPUT_DIVIDER_CLASS,
    CENTER_CONTENT
} from '@/constants/styles';
import { isSuccess } from '@/types/api';
import { useRouter } from 'next/navigation';


export async function getProfile(): Promise<User | null> {
    const res = await fetch('/api/mypage/profile', { credentials: 'include' });
    if (!res.ok) return null;
    const json = await res.json();
    return isSuccess(json) ? (json.data as User) : null;
}

export async function updateProfile(data: ProfileUpdateRequest) {
    const res = await fetch('/api/mypage/profile', {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(data),
    });
    return res.json(); // { code, message, data }
}


export default function MyPageProfile({ initialMember }: { initialMember: User }) {
    const router = useRouter();
    const [form, setForm] = useState<ProfileUpdateRequest>({
        name: initialMember.name,
        nickname: initialMember.nickname,
        phone: initialMember.phone,
        address: initialMember.address,
    });
    const [pending, setPending] = useState(false);

    const onChange =
        (k: keyof ProfileUpdateRequest) =>
            (e: React.ChangeEvent<HTMLInputElement>) => {
                const v = e.target.value;
                setForm(prev => ({ ...prev, [k]: v ? v : null }));
            };

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (pending) return;
        setPending(true);
        try {
            showToast.loading('회원 정보를 수정하려고 해요', { group: 'auth.update' });
            const res = await updateProfile(form);
            if (res?.code === 'SUCCESS') {
                showToast.success('회원 정보를 수정했어요', { group: 'auth.update' });

                // 1) 서버 정규화 결과 동기화
                const fresh = await getProfile();
                if (fresh) {
                    setForm({
                        name: fresh.name,
                        nickname: fresh.nickname,
                        phone: fresh.phone,
                        address: fresh.address,
                    });
                }

                // 2) 전역 반영이 필요하면(예: 헤더에 닉네임 표기) SSR 재검증
                // 필요 없으면 이 줄은 제거
                router.refresh();
            } else {
                showToast.error(res?.message || '수정에 실패했어요', { group: 'auth.update' });
            }
        } catch {
            showToast.error('서버 오류가 발생했어요', { group: 'auth.update' });
        } finally {
            setPending(false);
        }
    };

    return (
        <form
            onSubmit={onSubmit}
            className={`${FORM_CLASS} ${CENTER_CONTENT}`}
        >
            <h1 className="text-2xl font-semibold text-text-primary dark:text-dark-text-primary mb-4 text-center">
                프로필
            </h1>
            <div className="mb-8 text-sm text-text-secondary dark:text-dark-text-secondary">
                <div>이메일: {initialMember.email}</div>
                <div>등급: {initialMember.grade}</div>
                <div>인증: {initialMember.verified ? '완료' : '미완료'}</div>
                <div>가입일: {new Date(initialMember.createdAt).toLocaleString()}</div>
            </div>
            <div className={`mb-6 ${INPUT_GROUP_CLASS}`}>
                <input
                    className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                    placeholder="이름"

                    onChange={onChange('name')}
                />
                <input
                    className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                    placeholder="닉네임"
                    defaultValue={form.nickname ?? ''}
                    onChange={onChange('nickname')}
                />
                <input
                    className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                    placeholder="전화번호"
                    defaultValue={form.phone ?? ''}
                    onChange={onChange('phone')}
                />
                <input
                    className={`${INPUT_CLASS}`}
                    placeholder="주소"
                    defaultValue={form.address ?? ''}
                    onChange={onChange('address')}
                />
            </div>
            <Button type="submit" full variant="solid" size="md" disabled={pending}>
                저장
            </Button>
        </ form>
    );
}
