// /lib/api/wishlist.client.ts
import type { ProductSummary } from '@/types/product';

export async function addToWishlist(productId: number) {
    const res = await fetch(`/api/wishlist/${productId}`, {
        method: 'POST',
        credentials: 'include',
    });
    if (!res.ok) {
        const body = await safeJson(res);
        throw new Error(body?.message || 'wishlist add failed');
    }
}

export async function removeFromWishlist(productId: number) {
    const res = await fetch(`/api/wishlist/${productId}`, {
        method: 'DELETE',
        credentials: 'include',
    });
    if (!res.ok) {
        const body = await safeJson(res);
        throw new Error(body?.message || 'wishlist remove failed');
    }
}

async function safeJson(res: Response) {
    try { return await res.json(); } catch { return null; }
}
