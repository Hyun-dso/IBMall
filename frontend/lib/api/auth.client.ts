// /lib/api/auth.client.ts
'use client';

import { http } from '@/lib/api/http';
import type { ApiResponse } from '@/types/api';

export interface SignInPayload { email: string; password: string; }

export function signIn(body: SignInPayload): Promise<ApiResponse<null>> {
  // 명세: 200 {message:'로그인 성공'} (data 없음) → ApiResponse<null>로 정규화
  return http.post<null>('/api/auth/signin', body, {
    timeoutMs: 15000,
    okMessage: '로그인 성공',
    failMessage: '로그인 실패',
  });
}

// 구글 URL API가 순수 {url:string}을 바로 기대한다면 그대로 두고 싶다면 별도 raw 호출을 쓰거나,
// 일관성 위해 ApiResponse<{url:string}>로 맞추는 둘 중 택1.
// 여기서는 기존 시그니처 유지가 목적이므로 그대로 둔다.
export function getGoogleAuthUrl() {
  // 필요 시 http.get<{url:string}>로 교체 가능(그 경우 호출부도 변경)
  return fetch('/api/oauth2/authorize/google', {
    method: 'GET',
    credentials: 'include',
  }).then(r => r.json() as Promise<{ url: string }>);
}

export function signout(): Promise<ApiResponse<null>> {
  return http.post<null>('/api/auth/signout', undefined, {
    timeoutMs: 15000,
    okMessage: '로그아웃 성공',
    failMessage: '로그아웃 실패',
  });
}
