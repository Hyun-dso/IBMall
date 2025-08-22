// /lib/api/seller-categories.client.ts
import type { Category } from '@/types/category';

interface ApiSuccess<T> { success: true; data: T; message?: string }
interface ApiError { success: false; error: { status: number; code: string; message: string } }
type AnyResponse<T> = ApiSuccess<T> | ApiError;

const BASE_PATH = '/api/admin/categories'; // 프론트에서 내부 프록시로 호출

async function readJsonOrText<T>(res: Response): Promise<{ json: AnyResponse<T> | null; text: string | null }> {
    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) {
        try { return { json: await res.json() as AnyResponse<T>, text: null }; } catch { }
    }
    try { return { json: null, text: await res.text() }; } catch { return { json: null, text: null }; }
}

export type CreateCategoryResult =
    | { ok: true; data: Category; message: string }
    | { ok: false; message: string; httpStatus?: number; code?: string };

export async function createSellerCategory(name: string): Promise<CreateCategoryResult> {
    try {
        const res = await fetch(BASE_PATH, {
            method: 'POST',
            credentials: 'include',                  // 쿠키 자동 전송
            headers: { 'content-type': 'application/json', accept: 'application/json' },
            body: JSON.stringify({ name }),
        });

        const { json, text } = await readJsonOrText<Category>(res);

        if (!res.ok) {
            const code = (json as ApiError | null)?.error?.code;
            const msg =
                (json as any)?.error?.message ||
                (json as any)?.message ||
                (text ? `${res.status} ${res.statusText}: ${text.slice(0, 200)}` : `${res.status} ${res.statusText}`);
            return { ok: false, message: msg, httpStatus: res.status, ...(code ? { code } : {}) };
        }

        const data = (json as ApiSuccess<Category> | null)?.data ?? null;
        if (!data) {
            const msg = (json as any)?.message || (text ?? '응답 데이터가 비어있습니다');
            return { ok: false, message: msg, httpStatus: res.status };
        }
        const message = (json as any)?.message ?? '카테고리 생성 완료';
        return { ok: true, data, message };
    } catch {
        return { ok: false, message: '네트워크 오류' };
    }
}

export async function fetchSellerCategories() {
    const res = await fetch('/api/admin/categories', { credentials: 'include' });
    if (!res.ok) throw new Error('카테고리 불러오기 실패');
    return res.json();
}