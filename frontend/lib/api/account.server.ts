import type { MemberDto } from '@/types/account';
import { parse } from 'cookie';

export async function getUserFromServer(cookie: string): Promise<MemberDto | null> {
    const parsed = parse(cookie);
    const accessToken = parsed.accessToken;
    try {
        if (!accessToken) return null;

        const res = await fetch(`http://localhost:8080/api/members/me`, {
            headers: {
                Cookie: `accessToken=${accessToken}`,
            },
            cache: 'no-store',
        });

        if (!res.ok) return null;

        const json = await res.json();
        return json.data;
    } catch {
        return null
    };
}

export const signOut = async (path: string | null) => {
    try {
        const res = await fetch(`/api/auth/signout`, {
            method: 'POST',
            credentials: 'include',
        });

        if (!res.ok) {
            const { message } = await res.json();
            return;
        }
        if (path === null) path = '/';
        window.location.href = path;
    } catch {
        return;
    }
};
