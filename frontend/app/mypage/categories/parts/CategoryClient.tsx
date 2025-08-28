'use client';

import { useState } from 'react';
import type { Category } from '@/types/catalog';
import { createCategoryClient, deleteCategoryClient } from '@/lib/api/seller-categories';
import { showToast } from '@/lib/toast';

export default function CategoryClient({
    initialCategories,
    loadError,
}: { initialCategories: Category[]; loadError: string | null }) {
    const [categories, setCategories] = useState<Category[]>(initialCategories);
    const [newCat, setNewCat] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [deletingId, setDeletingId] = useState<number | null>(null);

    const add = async () => {
        const name = newCat.trim();
        if (!name) return showToast.error('카테고리 이름을 입력해주세요');
        setSubmitting(true);
        const r = await createCategoryClient(name);
        setSubmitting(false);
        if (!r.ok) return showToast.error(r.message || name + ' 카테고리 추가를 실패했습니다', { group: 'categoryAdd' + name });
        setCategories(prev => [{ id: r.data.id, name: r.data.name }, ...prev]);
        showToast.success(name + ' 카테고리를 추가했습니다', { group: 'categoryAdd' + name });
        setNewCat('');
    };

    const remove = async (id: number, name: string) => {
        if (!confirm(name + ' 카테고리를 삭제하시겠습니까?')) return;
        setDeletingId(id);
        const r = await deleteCategoryClient(id);
        setDeletingId(null);
        if (!r.ok) {
            // 서버가 409(CONFLICT) 등을 보낼 수 있음
            showToast.error(r.message || name + ' 카테고리 삭제를 실패했습니다', { group: 'categoryRemove' + name });
            return;
        }
        showToast.success(r.message || name + '카테고리를 삭제했습니다', { group: 'categoryRemove' + name });
        setCategories(prev => prev.filter(c => c.id !== id));
    };

    return (
        <div className="bg-surface dark:bg-dark-surface border border-border dark:border-dark-border rounded-2xl p-4">
            <div className="flex gap-2 mb-3">
                <input
                    value={newCat}
                    onChange={(e) => setNewCat(e.target.value)}
                    placeholder="새 카테고리명"
                    className="flex-1 px-3 py-2 rounded-md border border-border dark:border-dark-border bg-white dark:bg-dark-background text-text-primary dark:text-dark-text-primary"
                />
                <button onClick={add} disabled={submitting}>추가</button>
            </div>

            {loadError && <p className="text-error mb-2">로딩 실패: {loadError}</p>}

            <ul className="max-h-[60vh] overflow-auto divide-y divide-border dark:divide-dark-border bg-white dark:bg-dark-background rounded-md border border-border dark:border-dark-border">
                {categories.length === 0 ? (
                    <li className="px-3 py-2 text-text-secondary dark:text-dark-text-secondary">표시할 카테고리가 없습니다.</li>
                ) : (
                    categories.map(c => (
                        <li key={c.id} className="px-3 py-2 flex items-center justify-between gap-2">
                            <span className="truncate">{c.name}</span>
                            <button
                                className="border-error"
                                onClick={() => remove(c.id, c.name)}
                                disabled={deletingId === c.id}
                                aria-busy={deletingId === c.id}
                            >
                                삭제
                            </button>
                        </li>
                    ))
                )}
            </ul>
        </div>
    );
}
