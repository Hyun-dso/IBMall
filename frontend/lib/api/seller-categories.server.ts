// /lib/api/admin-categories.server.ts
import { cookies, headers } from 'next/headers';
import { apiFetchServer } from '@/lib/api/http.server';
import type { Category } from '@/types/category';

type SuccessShape<T> = { success: true; data: T; message?: string };
type LegacySuccess<T> = { status: 'SUCCESS'; data: T };
type ErrorShape = { success: false; error: { status: number; code: string; message: string } };
type AnyResponse<T> = SuccessShape<T> | LegacySuccess<T> | ErrorShape | { status: 'FAIL' };

function isOk<T>(b: AnyResponse<T>): b is SuccessShape<T> | LegacySuccess<T> {
    // success=true 또는 status=SUCCESS 모두 허용
    // @ts-expect-error runtime narrow
    return (b && b.success === true) || (b && b.status === 'SUCCESS');
}

async function fetchCategories(path: string): Promise<Category[] | null> {
    const cookieHeader = cookies().toString();
    const h = await headers();

    try {
        const body = await apiFetchServer<AnyResponse<Category[]>>(path, {
            method: 'GET',
            headers: {
                cookie: cookieHeader,
                'user-agent': h.get('user-agent') ?? '',
                'x-forwarded-for': h.get('x-forwarded-for') ?? '',
            },
            timeoutMs: 15000,
        });

        if (!isOk(body)) return null;
        // 두 성공 형태 모두 data를 가짐
        return body.data ?? null;
    } catch {
        return null;
    }
}

/** 관리자 카테고리 목록 */
export async function fetchAdminCategories(): Promise<Category[] | null> {
    return fetchCategories('/api/admin/categories');
}

/** 셀러 카테고리 목록 */
export async function fetchSellerCategories(): Promise<Category[] | null> {
    return fetchCategories('/api/seller/categories');
}
