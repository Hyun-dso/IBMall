import type { Product, ProductStatus } from '@/types/catalog';
import { serverJSON, clientJSON, type JsonResult } from './http';
import type { CreateProductPayload, UpdateProductPayload } from '@/types/api-product';

export const PRODUCTS_PATH = '/api/admin/products';

type RawProduct = Partial<Product> & { productId?: number; id?: number };

function normalizeProduct(r: RawProduct): Product {
    const id = r.id ?? r.productId;
    if (id == null) throw new Error('Product id missing');
    return {
        id,
        categoryId: Number(r.categoryId),
        name: String(r.name ?? ''),
        price: Number(r.price ?? 0),
        stock: Number(r.stock ?? 0),
        description: (r).description ?? null,
        thumbnailUrl: (r).thumbnailUrl ?? null,
        status: (r).status ?? 'ACTIVE',
        images: Array.isArray((r).images) ? (r).images : [],
        createdAt: (r).createdAt ?? new Date().toISOString(),
    };
}

/* SSR */
export async function listProductsServer(): Promise<JsonResult<Product[]>> {
    const res = await serverJSON<[]>(PRODUCTS_PATH, { method: 'GET' });
    if (!res.ok) return res;
    try {
        return { ok: true, data: res.data.map(normalizeProduct), message: res.message ?? null };
    } catch {
        return { ok: false, status: 500, message: '정규화 오류' };
    }
}

/* CSR */
export function createProductClient(input: CreateProductPayload) {
    return clientJSON<{ id: number }>(PRODUCTS_PATH, {
        method: 'POST',
        headers: { 'content-type': 'application/json' },
        body: JSON.stringify(input),
    });
}


export async function setProductStatusClient(id: number | undefined, status: ProductStatus) {
    if (typeof id !== 'number' || Number.isNaN(id)) {
        return { ok: false, status: 400, message: '유효하지 않은 상품 ID' } as const;
    }
    const qs = new URLSearchParams({ status });
    return clientJSON<null>(`${PRODUCTS_PATH}/${id}/status?${qs}`, { method: 'PATCH' });
}

export function updateProductClient(id: number, payload: UpdateProductPayload) {
    return clientJSON<null>(`${PRODUCTS_PATH}/${id}`, {
        method: 'PUT',
        headers: { 'content-type': 'application/json' },
        body: JSON.stringify(payload),
    });
}

export function deleteProductClient(id: number) {
    return clientJSON<null>(`${PRODUCTS_PATH}/${id}`, { method: 'DELETE' });
}