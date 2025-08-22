// /lib/api/mypage.client.ts
import type { Member, UpdateMemberRequest } from '@/types/member';
import { isSuccess } from '@/types/api';

export async function getProfile(): Promise<Member | null> {
    const res = await fetch('/api/mypage/profile', { credentials: 'include' });
    if (!res.ok) return null;
    const json = await res.json();
    return isSuccess(json) ? (json.data as Member) : null;
}

export async function updateProfile(data: UpdateMemberRequest) {
    const res = await fetch('/api/mypage/profile', {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(data),
    });
    return res.json(); // { code, message, data }
}
