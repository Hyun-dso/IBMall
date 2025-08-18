// app/_lib/serverFetch.ts
import { headers, cookies } from 'next/headers';

/**
 * SSR 전용 fetch 헬퍼
 * - 상대 경로('/api/...')를 현재 요청의 절대 URL로 변환
 * - 사용자 쿠키 전달
 * - SSR 캐시 비활성화
 */
export async function serverFetch(path: string, init: RequestInit = {}) {
  const h = headers() as unknown as Headers; // 타입 단언 (동기 처리)

  const proto = h.get('x-forwarded-proto') ?? 'https';
  const host  = h.get('x-forwarded-host') ?? h.get('host');
  if (!host) {
    throw new Error('Cannot resolve host header for SSR fetch');
  }

  const base = `${proto}://${host}`;
  const url  = path.startsWith('http') ? path : `${base}${path}`;

  const hdrs = new Headers(init.headers || {});
  const cookieHeader = cookies().toString();
  if (cookieHeader) hdrs.set('Cookie', cookieHeader);

  const res = await fetch(url, {
    ...init,
    headers: hdrs,
    cache: 'no-store',       // SSR은 항상 최신
    credentials: 'include',  // 세션/쿠키 전달
  });

  return res;
}
