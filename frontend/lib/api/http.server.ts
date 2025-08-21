// /lib/api/http.server.ts
import { cookies } from 'next/headers';

const RAW_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || '';
const BASE = RAW_BASE.replace(/\/+$/, '');

function buildUrl(path: string) {
    if (/^https?:\/\//i.test(path)) return path;
    return BASE ? `${BASE}${path.startsWith('/') ? path : `/${path}`}` : path;
}

export interface ServerRequestOptions extends RequestInit {
    timeoutMs?: number;
}

/** 서버 쿠키에서 accessToken을 읽어 Bearer 문자열 반환 */
async function bearerFromCookie(): Promise<string | null> {
    const token = (await cookies()).get('accessToken')?.value || null;
    return token ? `Bearer ${token}` : null;
}

/** 로우 요청: 절대 throw 하지 않음. */
async function requestRaw(path: string, init?: ServerRequestOptions): Promise<Response> {
    const controller = new AbortController();
    const timer = init?.timeoutMs ? setTimeout(() => controller.abort(), init.timeoutMs) : undefined;

    try {
        const h = new Headers(init?.headers || {});
        const hasBody = !!init?.body;
        if (hasBody && !h.has('Content-Type')) h.set('Content-Type', 'application/json');

        if (!h.has('Authorization')) {
            const bearer = await bearerFromCookie();
            if (bearer) h.set('Authorization', bearer);
        }

        const res = await fetch(buildUrl(path), {
            cache: 'no-store',
            ...init,
            signal: init?.signal ?? controller.signal,
            headers: h,
        });
        return res; // throw 하지 않음
    } finally {
        if (timer) clearTimeout(timer);
    }
}

async function parseJson(res: Response): Promise<any> {
    try { return await res.json(); } catch { return null; }
}

/** 기존 제네릭(JSON) – 필요 시 유지 (여전히 throw 가능 구조면 사용 자제) */
export async function apiFetchServer<T>(path: string, init?: ServerRequestOptions): Promise<T> {
    const res = await requestRaw(path, init);
    if (!res.ok) {
        const body = await parseJson(res);
        const msg = body?.error?.message || body?.message || `HTTP ${res.status}`;
        throw new Error(msg);
    }
    return (await parseJson(res)) as T;
}

/** 관리자(success/error) 포맷 전용: 절대 throw 하지 않음 */
export async function apiFetchServerAdmin<T>(
    path: string,
    init?: ServerRequestOptions
): Promise<{ success: true; data: T; message: string | null } | { success: false; error: { status: number; code: string; message: string } }> {
    const res = await requestRaw(path, init);
    const body = await parseJson(res);

    // 서버가 이미 관리자 포맷을 준 경우 그대로 반환
    if (body && typeof body.success === 'boolean') {
        return body;
    }

    if (res.ok) {
        const data = (body && typeof body === 'object' && 'data' in body) ? (body as any).data as T : (body as T);
        const message = (body && typeof body?.message === 'string') ? body.message as string : null;
        return { success: true, data, message };
    }

    const message =
        body?.error?.message ||
        body?.message ||
        `HTTP ${res.status}`;
    const code =
        body?.error?.code ||
        body?.code ||
        'UNKNOWN';

    return {
        success: false,
        error: { status: res.status || 400, code, message },
    };
}
