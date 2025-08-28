'use client';

import { useEffect, useRef, useState } from 'react';
import type { Category, Product, ProductStatus } from '@/types/catalog';
import type { UpdateProductPayload } from '@/types/api-product';
import { updateProductClient } from '@/lib/api/seller-product';
import { showToast } from '@/lib/toast';

export default function EditProductDialog({
    open,
    product,
    categories,
    onClose,
    onSaved,
}: {
    open: boolean;
    product: Product;
    categories: Category[];
    onClose: () => void;
    onSaved: (p: Product) => void;
}) {
    const firstRef = useRef<HTMLInputElement>(null);

    const [form, setForm] = useState<{
        categoryId: number;
        name: string;
        price: string;
        stock: string;
        description: string;
        status: ProductStatus;
        imageUrls: string; // 콤마/개행 구분
    }>(() => ({
        categoryId: product.categoryId,
        name: product.name,
        price: String(product.price),
        stock: String(product.stock),
        description: product.description ?? '',
        status: product.status,
        imageUrls: (product.images || []).join(','),
    }));

    useEffect(() => {
        if (open) setTimeout(() => firstRef.current?.focus(), 0);
    }, [open]);

    if (!open) return null;

    const onSubmit = async () => {
        const price = Number(form.price);
        const stock = Number(form.stock);
        if (!form.name.trim() || !Number.isFinite(price) || !Number.isFinite(stock)) {
            showToast.error('상품명/가격/재고 확인하세요.', { group: 'categoryOption' + form.name });
            return;
        }

        const descRaw = form.description.trim();
        const description: string | null = descRaw.length > 0 ? descRaw : null;

        const imageUrlsArr = form.imageUrls
            .split(/[\n,]/)
            .map(s => s.trim())
            .filter(Boolean);

        const payload: UpdateProductPayload = {
            name: form.name.trim(),
            price,
            stock,
            categoryId: form.categoryId,
            status: form.status,
            description,
            ...(imageUrlsArr.length > 0 ? { imageUrls: imageUrlsArr } : {}),
        };

        const r = await updateProductClient(product.id, payload);
        if (!r.ok) {
            showToast.error(form.name + ' 상품 정보를 수정 실패했습니다.', { group: 'categoryOption' + form.name });
            return;
        }

        onSaved({
            ...product,
            name: payload.name,
            price: payload.price,
            stock: payload.stock,
            categoryId: payload.categoryId,
            status: payload.status,
            description: payload.description,
            images: imageUrlsArr.length > 0 ? imageUrlsArr : [],
        });
        showToast.success(form.name + ' 상품 정보 수정했습니다.', { group: 'categoryOption' + form.name });
        onClose();
    };

    return (
        <div className="fixed mt-24 inset-0 z-50">
            <div className="absolute inset-0 bg-black/30" onClick={onClose} />
            <div className="absolute inset-x-0 top-10 mx-auto w-[min(680px,92vw)] rounded-2xl border border-border dark:border-dark-border bg-surface dark:bg-dark-surface p-4 shadow-xl">
                <h3 className="text-lg font-bold mb-3">상품 수정</h3>

                <div className="grid gap-2 md:grid-cols-2">
                    <select
                        value={form.categoryId}
                        onChange={(e) => setForm(f => ({ ...f, categoryId: Number(e.target.value) }))}
                        className="px-3 py-2 rounded-md border border-border dark:border-dark-border bg-white dark:bg-dark-background"
                    >
                        {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                    </select>
                    <input
                        ref={firstRef}
                        value={form.name}
                        onChange={(e) => setForm(f => ({ ...f, name: e.target.value }))}
                        placeholder="상품명"
                        className="px-3 py-2 rounded-md border border-border dark:border-dark-border bg-white dark:bg-dark-background"
                    />
                    <input
                        value={form.price}
                        onChange={(e) => setForm(f => ({ ...f, price: e.target.value }))}
                        inputMode="numeric"
                        placeholder="가격"
                        className="px-3 py-2 rounded-md border border-border dark:border-dark-border bg-white dark:bg-dark-background"
                    />
                    <input
                        value={form.stock}
                        onChange={(e) => setForm(f => ({ ...f, stock: e.target.value }))}
                        inputMode="numeric"
                        placeholder="재고"
                        className="px-3 py-2 rounded-md border border-border dark:border-dark-border bg-white dark:bg-dark-background"
                    />
                    <textarea
                        value={form.description}
                        onChange={(e) => setForm(f => ({ ...f, description: e.target.value }))}
                        placeholder="설명"
                        className="md:col-span-2 px-3 py-2 rounded-md border border-border dark:border-dark-border bg-white dark:bg-dark-background"
                    />
                    <div className="grid grid-cols-2 gap-2 md:col-span-2">
                        <select
                            value={form.status}
                            onChange={(e) => setForm(f => ({ ...f, status: e.target.value as ProductStatus }))}
                            className="px-3 py-2 rounded-md border border-border dark:border-dark-border bg-white dark:bg-dark-background"
                        >
                            <option value="ACTIVE">ACTIVE</option>
                            <option value="INACTIVE">INACTIVE</option>
                        </select>
                        <input
                            value={form.imageUrls}
                            onChange={(e) => setForm(f => ({ ...f, imageUrls: e.target.value }))}
                            placeholder="이미지 URL들(쉼표/줄바꿈)"
                            className="px-3 py-2 rounded-md border border-border dark:border-dark-border bg-white dark:bg-dark-background"
                        />
                    </div>
                </div>

                <div className="mt-4 flex justify-end gap-2">
                    <button onClick={onClose}>취소</button>
                    <button onClick={onSubmit}>저장</button>
                </div>
            </div>
        </div>
    );
}
