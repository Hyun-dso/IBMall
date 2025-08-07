// /app/pay/member/page.tsx
'use client';

import { useSearchParams } from 'next/navigation';
import { useEffect, useState } from 'react';
import { Toaster, toast } from 'react-hot-toast';
import type { ProductLineItem } from '@/types/cart';
import ProductLineItemCard from '@/components/ProductLineItemCard';

interface User {
    name: string;
    email: string;
    phone: string;
    address: string;
}

export default function MemberPaymentPage() {
    const searchParams = useSearchParams();
    const productId = searchParams.get('productId');
    const [quantity, setQuantity] = useState<number>(1);

    const [user, setUser] = useState<User | null>(null);
    const [product, setProduct] = useState<ProductLineItem | null>(null);
    const [recipient, setRecipient] = useState({
        name: '',
        phone: '',
        address: '',
    });

    useEffect(() => {
        if (!productId) {
            toast.error('잘못된 접근입니다.');
            return;
        }

        const fetchData = async () => {
            try {
                // 유저 정보 조회
                const userRes = await fetch('/api/members/me', { credentials: 'include' });
                if (!userRes.ok) throw new Error('회원 정보를 불러오지 못했습니다.');
                const userData = await userRes.json();
                setUser(userData);

                // 상품 정보 조회
                const productRes = await fetch(`/api/products/${productId}`);
                if (!productRes.ok) throw new Error('상품 정보를 불러오지 못했습니다.');
                const productData = await productRes.json();

                setProduct({
                    productId: productData.data.product.productId,
                    name: productData.data.product.name,
                    price: productData.data.product.price,
                    timeSalePrice: productData.data.product.timeSalePrice ?? undefined,
                    isTimeSale: productData.data.product.isTimeSale === true,
                    thumbnailUrl: productData.data.product.thumbnailUrl,
                    quantity,
                    disableQuantityControls: false,
                    showDeleteButton: false,
                });

                setRecipient({
                    name: userData.name,
                    phone: userData.phone,
                    address: userData.address,
                });
            } catch (e: any) {
                toast.error(e.message || '에러 발생');
            }
        };

        fetchData();
    }, [productId]);

    const handleRecipientChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setRecipient(prev => ({ ...prev, [name]: value }));
    };

    const handleQuantityChange = (newQty: number) => {
        if (newQty < 1) return;
        setQuantity(newQty);
        setProduct(prev => prev ? { ...prev, quantity: newQty } : prev);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        toast.dismiss();
        toast.success('회원 결제 로직 연결 필요');
    };

    return (
        <main className="min-h-screen flex flex-col items-center justify-center px-4 py-8 bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
            <Toaster />

            <div className="w-full max-w-md mb-8">
                <h2 className="text-lg font-semibold mb-3">주문 상품</h2>
                {product && (
                    <ProductLineItemCard
                        item={product}
                        onQuantityChange={handleQuantityChange}
                    />
                )}
            </div>

            <form
                onSubmit={handleSubmit}
                className="w-full max-w-md p-8 bg-surface dark:bg-dark-surface rounded-lg shadow-md"
            >
                <h1 className="text-2xl font-bold mb-6 text-center">회원 결제</h1>

                {user && (
                    <div className="mb-8">
                        <h2 className="text-lg font-semibold mb-2">구매자 정보</h2>
                        <div className="border border-border dark:border-dark-border rounded-md overflow-hidden">
                            <input
                                value={user.name}
                                readOnly
                                className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent"
                            />
                            <input
                                value={user.email}
                                readOnly
                                className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent"
                            />
                            <input
                                value={user.phone}
                                readOnly
                                className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent"
                            />
                            <input
                                value={user.address}
                                readOnly
                                className="w-full px-4 py-3 bg-transparent"
                            />
                        </div>
                    </div>
                )}

                <div className="mb-8">
                    <h2 className="text-lg font-semibold mb-2">수령인 정보</h2>
                    <div className="border border-border dark:border-dark-border rounded-md overflow-hidden">
                        <input
                            name="name"
                            placeholder="이름"
                            value={recipient.name}
                            onChange={handleRecipientChange}
                            required
                            className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent"
                        />
                        <input
                            name="phone"
                            placeholder="전화번호"
                            value={recipient.phone}
                            onChange={handleRecipientChange}
                            required
                            className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent"
                        />
                        <input
                            name="address"
                            placeholder="주소"
                            value={recipient.address}
                            onChange={handleRecipientChange}
                            required
                            className="w-full px-4 py-3 bg-transparent"
                        />
                    </div>
                </div>

                <button
                    type="submit"
                    className="w-full py-2 font-bold bg-primary text-text-primary dark:bg-primary dark:text-dark-text-primary rounded hover:opacity-90"
                >
                    결제 진행
                </button>
            </form>
        </main>
    );
}
