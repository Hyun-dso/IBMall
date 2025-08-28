'use client';

import { useMemo, useState } from 'react';
import type { Category, Product, ProductStatus } from '@/types/catalog';
import { createProductClient, deleteProductClient, setProductStatusClient } from '@/lib/api/seller-product';
import { cn } from '@/lib/utils';
import { CreateProductPayload } from '@/types/api-product';
import EditProductDialog from './EditProductDialog';
import { showToast } from '@/lib/toast';

type SortKey = keyof Pick<Product, 'id' | 'name' | 'price' | 'stock' | 'createdAt' | 'status'>;
type SortState = { key: SortKey; dir: 'asc' | 'desc' };

export default function ProductClient({
    initialProducts,
    initialCategories,
    loadErrors,
}: {
    initialProducts: Product[];
    initialCategories: Category[];
    loadErrors: { products: string | null; categories: string | null };
}) {
    const [products, setProducts] = useState<Product[]>(initialProducts);
    const [categories] = useState<Category[]>(initialCategories);

    const [form, setForm] = useState<{
        categoryId: number | null; name: string; price: string; stock: string;
        description: string; status: ProductStatus; imageUrls: string;
    }>({
        categoryId: categories[0]?.id ?? null,
        name: '', price: '', stock: '',
        description: '', status: 'ACTIVE', imageUrls: '',
    });
    const [submitting, setSubmitting] = useState(false);
    const [editing, setEditing] = useState<Product | null>(null);
    const [sort, setSort] = useState<SortState>({ key: 'id', dir: 'desc' });
    const [filter, setFilter] = useState<'ALL' | ProductStatus>('ALL');

    const filtered = useMemo(
        () => (filter === 'ALL' ? products : products.filter(p => p.status === filter)),
        [products, filter]
    );

    const sorted = useMemo(() => {
        const arr = [...filtered];
        const { key, dir } = sort; const mul = dir === 'asc' ? 1 : -1;
        arr.sort((a, b) => {
            const va = a[key], vb = b[key];
            if (key === 'name' || key === 'status') return String(va).localeCompare(String(vb)) * mul;
            if (key === 'createdAt') return (new Date(va).getTime() - new Date(vb).getTime()) * mul;
            return (Number(va) - Number(vb)) * mul;
        });
        return arr;
    }, [filtered, sort]);

    const header = (k: SortKey, label: string) => {
        const active = sort.key === k;
        return (
            <button
                type="button"
                onClick={() => setSort(s => (s.key === k ? { key: k, dir: s.dir === 'asc' ? 'desc' : 'asc' } : { key: k, dir: 'asc' }))}
                className={cn('w-full text-left px-3 py-2 hover:bg-surface dark:hover:bg-dark-surface', active && 'font-semibold')}
            >
                {label} {active ? (sort.dir === 'asc' ? '▲' : '▼') : ''}
            </button>
        );
    };

    const create = async () => {
        if (!form.categoryId) {
            showToast.error('카테고리를 선택하세요.', { group: 'categorySelect' }); return;
        }

        const price = Number(form.price);
        const stock = Number(form.stock);
        if (!form.name.trim() || !Number.isFinite(price) || !Number.isFinite(stock)) {
            showToast.error('상품명/가격/재고 확인하세요.', { group: 'categoryOption' });
            return;
        }

        const descRaw = form.description.trim();
        const description: string | null = descRaw.length > 0 ? descRaw : null;

        const imageUrlsArr = form.imageUrls
            .split(/[\n,]/)
            .map(s => s.trim())
            .filter(Boolean);

        // exactOptionalPropertyTypes 대응: 빈 배열이면 키 자체를 생략
        const payload: CreateProductPayload = {
            name: form.name.trim(),
            price,
            stock,
            categoryId: form.categoryId,
            status: form.status,
            description,
            ...(imageUrlsArr.length > 0 ? { imageUrls: imageUrlsArr } : {}),
        };

        const r = await createProductClient(payload);
        if (!r.ok) {
            showToast.error(payload.name + ' 상품을 등록 실패했습니다.', { group: 'productCreate' + payload.name }); return;
        }

        const created: Product = {
            id: r.data.id, categoryId: form.categoryId,
            name: form.name.trim(), price, stock,
            description: form.description.trim() || null,
            status: form.status, thumbnailUrl: null, images: imageUrlsArr,
            createdAt: new Date().toISOString(),
        };
        setProducts(prev => [created, ...prev]);
        setForm(f => ({ ...f, name: '', price: '', stock: '', description: '', imageUrls: '' }));
        showToast.success(payload.name + ' 상품을 등록했습니다.', { group: 'productCreate' + payload.name });
    };

    const toggleStatus = async (p: Product) => {
        const next: ProductStatus = p.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
        const r = await setProductStatusClient(p.id, next);
        if (!r.ok) {
            showToast.error(p.status + '상태 변경 실패', { group: 'productStatus' + p.status }); return;
        }
        showToast.success(p.name + ' 상품의 상태를 ' + p.status + '로 변경했습니다', { group: 'productStatus' + p.name });
        setProducts(list => list.map(x => x.id === p.id ? { ...x, status: next } : x));
    };

    const remove = async (p: Product) => {
        if (!confirm('삭제하시겠습니까?')) return;
        const r = await deleteProductClient(p.id);
        if (!r.ok) {
            showToast.error(p.name + ' 상품 삭제를 실패했습니다', { group: 'productRemove' + p.name }); return;
        }
        setProducts(list => list.filter(x => x.id !== p.id));
        showToast.success(p.name + ' 상품을 삭제했습니다', { group: 'productRemove' + p.name });
    };

    return (
        <div className="space-y-4">
            <div className="bg-surface dark:bg-dark-surface border border-border dark:border-dark-border rounded-2xl p-4">
                <div className="grid gap-2 md:grid-cols-2">
                    <select
                        value={form.categoryId ?? ''}
                        onChange={(e) => setForm(f => ({ ...f, categoryId: e.target.value ? Number(e.target.value) : null }))}
                        className="px-3 py-2 rounded-md border border-border dark:border-dark-border bg-white dark:bg-dark-background"
                    >
                        <option value="">카테고리 선택</option>
                        {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
                    </select>
                    <input
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
                        placeholder="설명(선택)"
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
                            placeholder="이미지 URL들(쉼표/줄바꿈, 선택)"
                            className="px-3 py-2 rounded-md border border-border dark:border-dark-border bg-white dark:bg-dark-background"
                        />
                    </div>
                    <div className="md:col-span-2">
                        <button onClick={create} disabled={submitting || !form.categoryId} className="w-full">등록</button>
                    </div>
                </div>
            </div>

            <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                    <span className="text-text-secondary dark:text-dark-text-secondary">상태</span>
                    <select
                        value={filter}
                        onChange={(e) => setFilter(e.currentTarget.value as ProductStatus)}
                        className="px-2 py-1 rounded-md border border-border dark:border-dark-border bg-white dark:bg-dark-background"
                    >
                        <option value="ALL">ALL</option>
                        <option value="ACTIVE">ACTIVE</option>
                        <option value="INACTIVE">INACTIVE</option>
                    </select>
                </div>
                {loadErrors.products && <span className="text-error">로딩 실패: {loadErrors.products}</span>}
            </div>

            <div className="rounded-md border border-border dark:border-dark-border overflow-hidden">
                <div className="grid grid-cols-8 bg-surface dark:bg-dark-surface border-b border-border dark:border-dark-border">
                    <div className="col-span-1">{header('id', 'ID')}</div>
                    <div className="col-span-2">{header('name', '이름')}</div>
                    <div className="col-span-1">{header('price', '가격')}</div>
                    <div className="col-span-1">{header('stock', '재고')}</div>
                    <div className="col-span-1">{header('status', '상태')}</div>
                    <div className="col-span-2">{header('createdAt', '생성일')}</div>
                </div>

                <ul className="divide-y divide-border dark:divide-dark-border bg-white dark:bg-dark-background">
                    {sorted.length === 0 ? (
                        <li className="px-3 py-2 text-text-secondary dark:text-dark-text-secondary">표시할 상품이 없습니다.</li>
                    ) : (
                        sorted.map((p) => (
                            <li key={p.id} className="px-3 py-2">
                                <div className="grid grid-cols-8 items-center">
                                    <div className="col-span-1">{p.id}</div>
                                    <div className="col-span-2 truncate">{p.name}</div>
                                    <div className="col-span-1">{p.price.toLocaleString()}</div>
                                    <div className="col-span-1">{p.stock}</div>
                                    <div className="col-span-1">
                                        <button
                                            onClick={() => toggleStatus(p)}
                                            aria-pressed={p.status === 'ACTIVE'}
                                            title={p.status === 'ACTIVE' ? '현재 ACTIVE (클릭 시 INACTIVE)' : '현재 INACTIVE (클릭 시 ACTIVE)'}
                                            className={cn(
                                                'px-2 py-1 select-none',
                                                p.status === 'ACTIVE' ? 'border-success dark:border-success' : 'border-warning'
                                            )}
                                        >
                                            {p.status}
                                        </button>
                                    </div>
                                    <div className="col-span-2 flex items-center gap-2">
                                        <span>{new Date(p.createdAt).toLocaleString()}</span>

                                        <button onClick={() => setEditing(p)} >수정</button>
                                        {editing && (
                                            <EditProductDialog
                                                open={!!editing}
                                                product={editing}
                                                categories={categories}
                                                onClose={() => setEditing(null)}
                                                onSaved={(updated) => {
                                                    setProducts(list => list.map(x => x.id === updated.id ? updated : x));
                                                }}
                                            />
                                        )}
                                        <button onClick={() => remove(p)} className="border-error dark:border-error">
                                            삭제
                                        </button>
                                    </div>
                                </div>

                            </li>
                        ))
                    )}
                </ul>
            </div>

            {loadErrors.categories && (
                <p className="text-error">카테고리 로딩 실패: {loadErrors.categories}</p>
            )}
        </div>
    );
}
