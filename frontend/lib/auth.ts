// /lib/auth.ts
import { cookies } from 'next/headers';

const API_BASE_URL = process.env.API_BASE_URL;
if (!API_BASE_URL) {
    throw new Error('환경 변수 API_BASE_URL이 정의되지 않았습니다.');
}

export async function getUserFromSession(cookie: string) {
    const res = await fetch(`${API_BASE_URL}/api/members/me`, {
        method: 'GET',
        headers: {
            Cookie: cookie,
        },
        credentials: 'include',
        cache: 'no-store',
    });

    if (!res.ok) return null;

    try {
        const data = await res.json();
        if (!data || !data.email || typeof data.name !== 'string') return null;
        return data;
    } catch {
        return null;
    }
}
