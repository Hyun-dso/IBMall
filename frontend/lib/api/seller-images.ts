import { clientJSON } from './http';

export const IMAGES_BASE = '/api/admin/images';

export function uploadImagesClient(productId: number, files: File[]) {
    const fd = new FormData();
    fd.append('productId', String(productId));
    files.forEach((f) => fd.append('files', f));
    return clientJSON<string[]>(`${IMAGES_BASE}/upload`, {
        method: 'POST',
        body: fd, // Content-Type 자동 설정(multipart/form-data)
    });
}

export function setThumbnailClient(productId: number, imageId: number) {
    const body = new URLSearchParams({ productId: String(productId), imageId: String(imageId) });
    return clientJSON<null>(`${IMAGES_BASE}/set-thumbnail`, {
        method: 'POST',
        headers: { 'content-type': 'application/x-www-form-urlencoded' },
        body,
    });
}

export function updateImageOrderClient(items: Array<{ imageId: number; sortOrder: number }>) {
    return clientJSON<null>(`${IMAGES_BASE}/update-order`, {
        method: 'POST',
        headers: { 'content-type': 'application/json' },
        body: JSON.stringify(items),
    });
}
