// /lib/api/http.ts
import type { ApiResponse } from '@/types/api';

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || '';

function withTimeout<T>(p: Promise<T>, ms: number) {
    return new Promise<T>((resolve, reject) => {
        const id = setTimeout(() => reject(new Error('TIMEOUT')), ms);
        p.then(
            v => { clearTimeout(id); resolve(v); },
            e => { clearTimeout(id); reject(e); }
        );
    });
}

async function parseJson(res: Response) {
    try { return await res.json(); } catch { return null; }
}

function toApiResponse<T>(res: Response, body: any, okMsg: string, failMsg: string): ApiResponse<T> {
    const message =
        (body && typeof body.message === 'string' && body.message) ||
        (res.ok ? okMsg : failMsg);

    if (res.ok) {
        return { code: 'SUCCESS', message, data: (body?.data ?? null) as unknown as T };
    }
    return { code: 'FAIL', message, data: null };
}

type Method = 'GET' | 'POST' | 'PATCH' | 'DELETE';
type ReqOpts = {
    timeoutMs?: number;
    okMessage?: string;
    failMessage?: string;
    headers?: Record<string, string>;
};

async function request<T>(
    method: Method,
    url: string,
    body?: unknown,
    opts?: ReqOpts
): Promise<ApiResponse<T>> {
    const timeoutMs = opts?.timeoutMs ?? 15000;
    const okMessage = opts?.okMessage ?? '요청 성공';
    const failMessage = opts?.failMessage ?? '요청 실패';

    const init: RequestInit = {
        method,                        // string로 OK
        credentials: 'include',
        cache: 'no-store',
        headers: {
            ...(opts?.headers ?? {}),
            ...(body != null ? { 'Content-Type': 'application/json' } : {}),
        },
    };

    // exactOptionalPropertyTypes 대응: body 키 자체를 조건부로 추가
    if (body != null) {
        init.body = JSON.stringify(body);
    }

    const res = await withTimeout(fetch(`${BASE_URL}${url}`, init), timeoutMs);
    const json = await parseJson(res);
    return toApiResponse<T>(res, json, okMessage, failMessage);
}

export const http = {
    get<T>(url: string, opts?: ReqOpts) {
        return request<T>('GET', url, undefined, opts);
    },
    post<T>(url: string, body?: unknown, opts?: ReqOpts) {
        return request<T>('POST', url, body, opts);
    },
    patch<T>(url: string, body?: unknown, opts?: ReqOpts) {
        return request<T>('PATCH', url, body, opts);
    },
    delete<T>(url: string, body?: unknown, opts?: ReqOpts) {
        return request<T>('DELETE', url, body, opts);
    },
};
