// /lib/api/members.client.ts
'use client';

import { http } from '@/lib/api/http';
import type { SignupPayload, SignupResponse } from '@/types/api-members';

export function signup(body: SignupPayload) {
    return http.post<SignupResponse>('/api/members/signup', body, { timeoutMs: 15000 });
}
