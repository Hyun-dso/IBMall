import { cookies, headers } from 'next/headers';
import { apiFetchServer } from '@/lib/api/http.server';
import type { ApiResponse } from '@/types/api';
import { isSuccess } from '@/types/api';
import type { Member } from '@/types/member';

export async function getUserFromServer(): Promise<Member | null> {
  try {
    // ✅ headers()는 await
    const h = await headers(); // Promise<ReadonlyHeaders> -> ReadonlyHeaders
    // (선택) IDE 타입 끊김 시: const h = (await headers()) as unknown as Headers;

    const proto = h.get('x-forwarded-proto') ?? 'https';
    const host  = h.get('x-forwarded-host') ?? h.get('host') ?? 'ibmall.shop';
    const origin = `${proto}://${host}`;
    const url = `${origin}/api/members/me`;

    const cookieHeader = cookies().toString(); // cookies()는 동기

    const body = await apiFetchServer<ApiResponse<Member>>(url, {
      method: 'GET',
      headers: {
        cookie: cookieHeader,
        'user-agent': h.get('user-agent') ?? '',
        'x-forwarded-for': h.get('x-forwarded-for') ?? '',
      },
      timeoutMs: 15_000,
    });

    if (!isSuccess(body) || !body.data) return null;
    return body.data;
  } catch {
    return null;
  }
}
