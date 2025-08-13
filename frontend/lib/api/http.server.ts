// /lib/api/http.server.ts
// 서버 전용 HTTP 래퍼(쿠키 전달, Response → JSON 파싱 통일)
const RAW_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || '';
const BASE = RAW_BASE.replace(/\/+$/, '');

function buildUrl(path: string) {
    if (/^https?:\/\//i.test(path)) return path;
    return BASE ? `${BASE}${path.startsWith('/') ? path : `/${path}`}` : path;
}

export interface ServerRequestOptions extends RequestInit {
    timeoutMs?: number;
}

async function request(path: string, init?: ServerRequestOptions): Promise<Response> {
    const controller = new AbortController();
    const timer = init?.timeoutMs ? setTimeout(() => controller.abort(), init.timeoutMs) : undefined;
    try {
        const res = await fetch(buildUrl(path), {
            cache: 'no-store',
            ...init,
            signal: init?.signal ?? controller.signal,
            headers: {
                'Content-Type': 'application/json',
                ...(init?.headers ?? {}),
            },
        });
        if (!res.ok) {
            let msg = `HTTP ${res.status}`;
            try {
                const j = await res.json();
                if (j?.message) msg = j.message;
            } catch { }
            throw new Error(msg);
        }
        return res;
    } finally {
        if (timer) clearTimeout(timer);
    }
}

export async function apiFetchServer<T>(path: string, init?: ServerRequestOptions): Promise<T> {
    const res = await request(path, init);
    return res.json() as Promise<T>;
}
