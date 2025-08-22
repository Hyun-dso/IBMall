// /lib/api/seller.server.ts
import { apiFetchServer } from '@/lib/api/http.server';
import type { ApiResponse } from '@/types/api';
import { isSuccess } from '@/types/api';
import type { SellerDashboard, SellerOrder, SellerProduct, PageResult } from '@/types/seller';

// 주의: 실제 엔드포인트는 반드시 사용자 확인 필요.
const ENDPOINTS = {
    dashboard: '/api/seller/dashboard',
    orders: '/api/seller/orders',
    products: '/api/seller/products',
};

export async function getSellerDashboard(): Promise<SellerDashboard | null> {
    try {
        const res = await apiFetchServer<ApiResponse<SellerDashboard>>(ENDPOINTS.dashboard, { method: 'GET', timeoutMs: 15000 });
        if (!isSuccess(res)) return null;
        return res.data;
    } catch {
        return null;
    }
}

export async function listSellerOrders(params: { page: number; size: number }): Promise<PageResult<SellerOrder> | null> {
    try {
        const q = new URLSearchParams({ page: String(params.page), size: String(params.size) }).toString();
        const res = await apiFetchServer<ApiResponse<PageResult<SellerOrder>>>(`${ENDPOINTS.orders}?${q}`, { method: 'GET', timeoutMs: 15000 });
        if (!isSuccess(res)) return { items: [], page: 1, size: params.size, totalElements: 0, totalPages: 0 };
        return res.data;
    } catch {
        return { items: [], page: 1, size: params.size, totalElements: 0, totalPages: 0 };
    }
}

export async function listSellerProducts(params: { page: number; size: number }): Promise<PageResult<SellerProduct> | null> {
    try {
        const q = new URLSearchParams({ page: String(params.page), size: String(params.size) }).toString();
        const res = await apiFetchServer<ApiResponse<PageResult<SellerProduct>>>(`${ENDPOINTS.products}?${q}`, { method: 'GET', timeoutMs: 15000 });
        if (!isSuccess(res)) return { items: [], page: 1, size: params.size, totalElements: 0, totalPages: 0 };
        return res.data;
    } catch {
        return { items: [], page: 1, size: params.size, totalElements: 0, totalPages: 0 };
    }
}
