// /lib/api/products.server.ts
import { apiFetchServer } from '@/lib/api/http.server';
import type { ApiResponse } from '@/types/api';
import type { ProductSummary } from '@/types/product';
import { isSuccess } from '@/types/api';

export async function fetchProductSummaries(): Promise<ProductSummary[]> {
    const body = await apiFetchServer<ApiResponse<ProductSummary[]>>('/api/products?limit=24', {
        method: 'GET',
        headers: {},
        timeoutMs: 15000,
    });

    if (!isSuccess(body)) return [];
    return body.data;
}
