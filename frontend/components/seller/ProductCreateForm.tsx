// /components/seller/ProductCreateForm.tsx
'use client';

import { useState } from 'react';
import type { Category } from '@/types/category';
import Button from '@/components/ui/Button';
import { showToast } from '@/lib/toast';
import { createSellerCategory, fetchSellerCategories } from '@/lib/api/seller-categories.client';
import { INPUT_CLASS, INPUT_DIVIDER_CLASS, INPUT_GROUP_CLASS, CENTER_CONTENT, FORM_CLASS } from '@/constants/styles';

type Props = { initialCategories: Category[] }; // ← 필수

export default function ProductCreateForm({ initialCategories }: Props) {
    const [form, setForm] = useState({ name: '', price: '', stock: '' });
    const [cats, setCats] = useState<Category[]>(initialCategories);
    const [catId, setCatId] = useState<number | ''>(initialCategories[0]?.id ?? '');
    const [newCat, setNewCat] = useState('');
    const [pendingCat, setPendingCat] = useState(false);

    const addCategory = async () => {
        const n = newCat.trim();
        if (!n) { showToast.error('카테고리명을 입력하세요.'); return; }
        setPendingCat(true);
        try {
            const r = await createSellerCategory(n);
            if (!r.ok) { showToast.error(r.message); return; }
            const created = r.data;
            const next = [...cats, created].sort((a, b) => a.id - b.id);
            setCats(next);
            setCatId(created.id);
            setNewCat('');
            showToast.success('카테고리 생성 완료');
        } finally {
            setPendingCat(false);
        }
    };

    const submitProduct = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!catId) { showToast.error('카테고리를 선택하세요.'); return; }
        showToast.error('상품 등록 API 연결 필요');
    };

    const loadCategories = async () => {
        try {
            const r = await fetchSellerCategories();
            setCats(r.data);
        } catch (err) {
            showToast.error('카테고리 불러오기 실패');
        }
    };

    return (
        <form onSubmit={submitProduct} className="border border-border dark:border-dark-border rounded-xl p-6 bg-surface dark:bg-dark-surface space-y-4">
            <h1 className="text-lg font-semibold">새 상품 등록</h1>

            <label className="block">
                <span className="text-sm text-text-secondary dark:text-dark-text-secondary">카테고리</span>
                <select
                    className="mt-1 w-full px-4 py-3 bg-transparent border border-border dark:border-dark-border rounded-lg"
                    value={catId}
                    onFocus={loadCategories}   // 포커스 시 로드
                    onChange={(e) => setCatId(e.target.value ? Number(e.target.value) : '')}
                >
                    {cats.map((c) => (
                        <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                </select>
            </label>

            <div className={`${INPUT_GROUP_CLASS}`}>
                <input
                    value={newCat}
                    onChange={(e) => setNewCat(e.target.value)}
                    placeholder="새 카테고리명"
                    className={`${INPUT_CLASS}`}
                />
            </div>
            <Button type="button" size="md" variant="outline" disabled={pendingCat} onClick={addCategory}>
                {pendingCat ? '생성 중' : '카테고리 추가'}
            </Button>

            <div className={`mb-6 ${INPUT_GROUP_CLASS}`}>
                <input className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                    placeholder="상품명"
                    value={form.name}
                    onChange={(e) => setForm({ ...form, name: e.target.value })} />
                <input className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                    placeholder="가격" inputMode="numeric"
                    value={form.price}
                    onChange={(e) => setForm({ ...form, price: e.target.value })} />
                <input className={`${INPUT_CLASS}`}
                    placeholder="재고" inputMode="numeric"
                    value={form.stock}
                    onChange={(e) => setForm({ ...form, stock: e.target.value })} />
            </div>
            <Button type="submit" full variant="solid" size="md">등록</Button>
        </form >
    );
}
