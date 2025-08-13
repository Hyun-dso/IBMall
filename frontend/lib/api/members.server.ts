// /lib/api/members.server.ts
import { cookies, headers } from 'next/headers';
import { apiFetchServer } from '@/lib/api/http.server';
import type { ApiResponse } from '@/types/api';
import { isSuccess } from '@/types/api';
import type { Member } from '@/types/member';

export async function getUserFromServer(): Promise<Member | null> {
    const cookieHeader = cookies().toString();
    const h = await headers();

    const body = await apiFetchServer<ApiResponse<Member>>('/api/members/me', {
        method: 'GET',
        headers: {
            cookie: cookieHeader,
            'user-agent': h.get('user-agent') ?? '',
            'x-forwarded-for': h.get('x-forwarded-for') ?? '',
        },
        timeoutMs: 15000,
    });

    if (!isSuccess(body)) return null;
    return body.data;
}
