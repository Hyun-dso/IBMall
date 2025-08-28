import type { Category } from '@/types/catalog';
import { serverJSON, clientJSON } from './http';

export const CATEGORIES_PATH = '/api/admin/categories';

export function listCategoriesServer() {
    return serverJSON<Category[]>(CATEGORIES_PATH, { method: 'GET' });
}

export function createCategoryClient(name: string) {
    return clientJSON<Category>(CATEGORIES_PATH, {
        method: 'POST',
        headers: { 'content-type': 'application/json' },
        body: JSON.stringify({ name }),
    });
}
/** 카테고리 삭제 */
export function deleteCategoryClient(id: number) {
    return clientJSON<null>(`${CATEGORIES_PATH}/${id}`, { method: 'DELETE' });
}