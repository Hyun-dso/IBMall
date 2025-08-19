// /lib/api/seller-categories.client.ts
import type { Category } from '@/types/category';

interface ApiSuccess<T> { success: true; data: T; message?: string }
interface ApiError { success: false; error: { status: number; code: string; message: string } }
interface LegacySuccess<T> { status: 'SUCCESS'; data: T; message?: string }
type AnyResponse<T> = ApiSuccess<T> | ApiError | LegacySuccess<T>;

const BASE_PATH = '/api/admin/categories';

function isApiSuccess<T>(b: AnyResponse<T> | null | undefined): b is ApiSuccess<T> {
    return !!b && (b as ApiSuccess<T>).success === true;
}
function isLegacySuccess<T>(b: AnyResponse<T> | null | undefined): b is LegacySuccess<T> {
    return !!b && (b as LegacySuccess<T>).status === 'SUCCESS';
}
function pickMessage<T>(b: AnyResponse<T> | null | undefined): string {
    if (!b) return '서버 응답 없음';
    if (isApiSuccess<T>(b) || isLegacySuccess<T>(b)) return (b as any).message ?? '요청 처리 완료';
    return (b as ApiError).error?.message ?? '요청 실패';
}
async function readJson<T>(res: Response): Promise<AnyResponse<T> | null> {
    try { return (await res.json()) as AnyResponse<T>; } catch { return null; }
}

export type CreateCategoryResult =
    | { ok: true; data: Category; message: string }
    | { ok: false; message: string; httpStatus?: number }                // code 없음
    | { ok: false; message: string; httpStatus?: number; code: string }; // code 있을 때만 존재

export interface CreateCategoryOptions {
    signal?: AbortSignal | null;
    timeoutMs?: number;
}

export async function createSellerCategory(
    name: string,
    opts: CreateCategoryOptions = {}
): Promise<CreateCategoryResult> {
    const controller = !opts.signal && opts.timeoutMs ? new AbortController() : null;
    const timer = controller ? setTimeout(() => controller.abort(), opts.timeoutMs) : null;

    try {
        const init: RequestInit = {
            method: 'POST',
            credentials: 'include',
            headers: { 'content-type': 'application/json' },
            body: JSON.stringify({ name }),
        };
        if (controller) init.signal = controller.signal;
        else if (opts.signal) init.signal = opts.signal;

        const res = await fetch('localhost:8080' + BASE_PATH, init);
        const body = await readJson<Category>(res);

        if (!res.ok) {
            const msg = pickMessage(body);
            const code = (body as ApiError | null)?.error?.code as string | undefined;

            // exactOptionalPropertyTypes 대응: code가 있을 때만 속성 추가
            const base = { ok: false as const, message: msg, httpStatus: res.status };
            const result: CreateCategoryResult = code ? { ...base, code } : base;
            return result;
        }

        const data =
            (isApiSuccess<Category>(body) && body.data) ||
            (isLegacySuccess<Category>(body) && body.data) ||
            null;

        if (!data) {
            return { ok: false, message: '응답 데이터가 비어있습니다', httpStatus: res.status };
        }

        return { ok: true, data, message: pickMessage(body) };
    } catch (e) {
        if (e instanceof DOMException && e.name === 'AbortError') {
            return { ok: false, message: '요청이 취소되었습니다' };
        }
        return { ok: false, message: '네트워크 오류' };
    } finally {
        if (timer) clearTimeout(timer);
    }
}
