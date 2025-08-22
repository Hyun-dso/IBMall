// /lib/api/members.client.ts
'use client';

import { http } from '@/lib/api/http';
import type { SignupPayload, SignupResponse } from '@/types/api-members';

export function signup(body: SignupPayload) {
    return http.post<SignupResponse>('/api/members/signup', body, { timeoutMs: 15000 });
}

// /lib/api/members.client.ts
import type { ApiResponse } from '@/types/api';
import type { Member } from '@/types/member';

const RAW_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || '';
const BASE = RAW_BASE.replace(/\/+$/, '');

async function safeJson<T>(res: Response): Promise<T | null> {
  try { return await res.json(); } catch { return null; }
}

export async function getMeClient(): Promise<Member | null> {
  const controller = new AbortController();
  const id = setTimeout(() => controller.abort(), 15000);

  try {
    const res = await fetch(`${BASE}/api/members/me`, {
      method: 'GET',
      credentials: 'include',
      signal: controller.signal,
    });

    if (!res.ok) return null;

    const body = await safeJson<ApiResponse<Member>>(res);
    if (!body || body.code !== 'SUCCESS') return null;
    return body.data;
  } catch {
    return null;
  } finally {
    clearTimeout(id);
  }
}
