// /lib/api/http.ts
import type { ApiResponse, ApiResponseAdmin } from '@/types/api';

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || '';

function withTimeout<T>(p: Promise<T>, ms: number) {
    return new Promise<T>((resolve, reject) => {
        const id = setTimeout(() => reject(new Error('TIMEOUT')), ms);
        p.then(
            (v) => { clearTimeout(id); resolve(v); },
            (e) => { clearTimeout(id); reject(e); }
        );
    });
}

async function parseJson(res: Response): Promise<unknown | null> {
    try { return await res.json(); } catch { return null; }
}

type Method = 'GET' | 'POST' | 'PATCH' | 'DELETE';
type ReqOpts = {
    timeoutMs?: number;
    okMessage?: string;
    failMessage?: string;
    headers?: Record<string, string>;
};

function isRecord(v: unknown): v is Record<string, unknown> {
    return typeof v === 'object' && v !== null;
}

function pickMessage(res: Response, body: unknown, okMsg: string, failMsg: string): string {
    const msg = isRecord(body) && typeof body.message === 'string' ? body.message : undefined;
    return msg ?? (res.ok ? okMsg : failMsg);
}

/* ---------- 레거시(SUCCESS/FAIL) 전용 유틸 ---------- */

/**
 * SUCCESS 분기에서는 'null'을 절대 반환하지 않는다.
 * data 필드가 없으면 undefined를 반환하여 T=void(기본값)와 호환.
 */
function pickData<T>(body: unknown): T {
    if (!isRecord(body)) return undefined as unknown as T;
    if ('data' in body) {
        const v = (body as { data?: unknown }).data;
        return v as T; // 호출부 제네릭이 책임
    }
    return undefined as unknown as T;
}

function toApiResponse<T>(
    res: Response,
    body: unknown,
    okMsg: string,
    failMsg: string
): ApiResponse<T> {
    const message = pickMessage(res, body, okMsg, failMsg);

    if (res.ok) {
        const data = pickData<T>(body); // T (undefined 가능), null 사용 안 함
        return { code: 'SUCCESS', message, data } as ApiResponse<T>;
    }

    return { code: 'FAIL', message, data: null } as ApiResponse<T>;
}

async function requestLegacy<T>(
    method: Method,
    url: string,
    body?: unknown,
    opts?: ReqOpts
): Promise<ApiResponse<T>> {
    const timeoutMs = opts?.timeoutMs ?? 15000;
    const okMessage = opts?.okMessage ?? '요청 성공';
    const failMessage = opts?.failMessage ?? '요청 실패';

    const init: RequestInit = {
        method,
        credentials: 'include',
        cache: 'no-store',
        headers: {
            ...(opts?.headers ?? {}),
            ...(body != null ? { 'Content-Type': 'application/json' } : {}),
        },
    };
    if (body != null) init.body = JSON.stringify(body);

    const res = await withTimeout(fetch(`${BASE_URL}${url}`, init), timeoutMs);
    const json = await parseJson(res);
    return toApiResponse<T>(res, json, okMessage, failMessage);
}

/* ---------- 관리자(success/error) 전용 유틸 ---------- */

function toAdminResponse<T>(
    res: Response,
    body: unknown,
    okMsg: string,
    failMsg: string
): ApiResponseAdmin<T> {
    // 서버가 이미 관리자 포맷을 반환한 경우 그대로 사용
    if (isRecord(body) && typeof body.success === 'boolean') {
        return body as unknown as ApiResponseAdmin<T>;
    }

    // 관리자 포맷이 아니어도 최대한 정규화하여 반환
    const message = pickMessage(res, body, okMsg, failMsg);

    if (res.ok) {
        // 성공: data 추출 시도
        const data = pickData<T>(body);
        return { success: true, data, message };
    }

    // 실패: status/코드/메시지 정규화
    const code =
        (isRecord(body) && typeof body.code === 'string' && (body.code as string)) ||
        (isRecord(body) && isRecord((body as any).error) && typeof (body as any).error.code === 'string' && (body as any).error.code) ||
        'UNKNOWN';

    return {
        success: false,
        error: {
            status: res.status || 400,
            code,
            message,
        },
    };
}

async function requestAdmin<T>(
    method: Method,
    url: string,
    body?: unknown,
    opts?: ReqOpts
): Promise<ApiResponseAdmin<T>> {
    const timeoutMs = opts?.timeoutMs ?? 15000;
    const okMessage = opts?.okMessage ?? '요청 성공';
    const failMessage = opts?.failMessage ?? '요청 실패';

    const init: RequestInit = {
        method,
        credentials: 'include',
        cache: 'no-store',
        headers: {
            ...(opts?.headers ?? {}),
            ...(body != null ? { 'Content-Type': 'application/json' } : {}),
        },
    };
    if (body != null) init.body = JSON.stringify(body);

    const res = await withTimeout(fetch(`${BASE_URL}${url}`, init), timeoutMs);
    const json = await parseJson(res);
    return toAdminResponse<T>(res, json, okMessage, failMessage);
}

/* ---------- 공개 인터페이스 ---------- */

export const http = {
    // 레거시(SUCCESS/FAIL) 포맷
    get<T = void>(url: string, opts?: ReqOpts) {
        return requestLegacy<T>('GET', url, undefined, opts);
    },
    post<T = void>(url: string, body?: unknown, opts?: ReqOpts) {
        return requestLegacy<T>('POST', url, body, opts);
    },
    patch<T = void>(url: string, body?: unknown, opts?: ReqOpts) {
        return requestLegacy<T>('PATCH', url, body, opts);
    },
    delete<T = void>(url: string, body?: unknown, opts?: ReqOpts) {
        return requestLegacy<T>('DELETE', url, body, opts);
    },
};

/** 관리자(success/error) 포맷 — error.code 접근을 보장 */
export const httpAdmin = {
    get<T = void>(url: string, opts?: ReqOpts) {
        return requestAdmin<T>('GET', url, undefined, opts);
    },
    post<T = void>(url: string, body?: unknown, opts?: ReqOpts) {
        return requestAdmin<T>('POST', url, body, opts);
    },
    patch<T = void>(url: string, body?: unknown, opts?: ReqOpts) {
        return requestAdmin<T>('PATCH', url, body, opts);
    },
    delete<T = void>(url: string, body?: unknown, opts?: ReqOpts) {
        return requestAdmin<T>('DELETE', url, body, opts);
    },
};

// 서버 전용 베이스 URL(절대경로)
export const API_BASE = (process.env.API_BASE_URL ?? process.env.NEXT_PUBLIC_API_BASE_URL ?? '').replace(/\/+$/, '');

export type JsonResult<T> =
    | { ok: true; data: T; message?: string | null }
    | { ok: false; status: number; message: string; code?: string };

function normalizeError(res: Response, body: any): { status: number; message: string; code?: string } {
    if (body && typeof body === 'object') {
        const msg = body?.message || body?.error?.message || res.statusText;
        const code = body?.error?.code;
        return { status: res.status, message: String(msg ?? ''), code };
    }
    return { status: res.status, message: `${res.status} ${res.statusText}` };
}

export async function serverJSON<T>(path: string, init?: RequestInit): Promise<JsonResult<T>> {
    const { cookies, headers } = await import('next/headers');
    if (!API_BASE) return { ok: false, status: 500, message: 'API_BASE_URL 미설정' };

    const cookie = (await cookies()).toString();
    const h = await headers();

    const forwardHost = h.get('host');
    const res = await fetch(`${API_BASE}${path}`, {
        ...init,
        headers: {
            accept: 'application/json',
            ...(init?.headers || {}),
            cookie,
            ...(forwardHost ? { 'x-forwarded-host': forwardHost } : {}),
        },
        cache: 'no-store',
    });

    const ct = res.headers.get('content-type') || '';
    const body = ct.includes('application/json') ? await res.json().catch(() => null) : await res.text().catch(() => null);
    if (!res.ok) return { ok: false, ...normalizeError(res, body) };

    const data = (body?.data ?? body) as T;
    return { ok: true, data, message: body?.message ?? null };
}

export async function clientJSON<T>(path: string, init?: RequestInit): Promise<JsonResult<T>> {
    const res = await fetch(path, {
        credentials: 'include',
        headers: { accept: 'application/json', ...(init?.headers || {}) },
        ...init,
    });
    const ct = res.headers.get('content-type') || '';
    const body = ct.includes('application/json') ? await res.json().catch(() => null) : await res.text().catch(() => null);
    if (!res.ok) return { ok: false, ...normalizeError(res, body) };
    const data = (body?.data ?? body) as T;
    return { ok: true, data, message: body?.message ?? null };
}