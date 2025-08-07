'use client';

import { useSearchParams } from 'next/navigation';
import { useEffect, useState } from 'react';
import { Toaster, toast } from 'react-hot-toast';
import type { GuestSinglePaymentRequest } from '@/types/payment';
import type { ProductLineItem } from '@/types/cart';
import ProductLineItemCard from '@/components/ProductLineItemCard';
import PortOne from '@portone/browser-sdk/v2';

export default function GuestPaymentPage() {
    const searchParams = useSearchParams();
    const productId = searchParams.get('productId');

    const [quantity, setQuantity] = useState(1);
    const [form, setForm] = useState<Partial<GuestSinglePaymentRequest>>({
        buyerName: '',
        buyerEmail: '',
        buyerPhone: '',
        buyerAddress: '',
        recipientName: '',
        recipientPhone: '',
        recipientAddress: '',
    });

    const [product, setProduct] = useState<ProductLineItem | null>(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (!productId) {
            toast.error('잘못된 접근입니다.');
            return;
        }

        const fetchProduct = async () => {
            try {
                setLoading(true);
                const res = await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/products/${productId}`);
                const json = await res.json();
                const data = json.data.product;

                const lineItem: ProductLineItem = {
                    productId: data.productId,
                    name: data.name,
                    price: data.price,
                    timeSalePrice: data.timeSalePrice ?? undefined,
                    isTimeSale: data.isTimeSale === true,
                    thumbnailUrl: data.thumbnailUrl,
                    quantity,
                    productOptionId: undefined,
                    optionName: undefined,
                    disableQuantityControls: false,
                    showDeleteButton: false,
                };

                setProduct(lineItem);
            } catch (err: any) {
                toast.error(err.message || '에러 발생');
            } finally {
                setLoading(false);
            }
        };

        fetchProduct();
    }, [productId]);

    const handleQuantityChange = (newQty: number) => {
        if (newQty < 1) return;
        setQuantity(newQty);
        setProduct(prev => (prev ? { ...prev, quantity: newQty } : prev));
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setForm(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        toast.dismiss();

        if (!product || !form.buyerName || !form.buyerEmail || !form.buyerPhone || !form.buyerAddress || !form.recipientName || !form.recipientPhone || !form.recipientAddress) {
            toast.error('모든 정보를 입력해 주세요.');
            return;
        }

        const orderUid = `order_${Date.now()}`;
        const amount = (product.isTimeSale && product.timeSalePrice ? product.timeSalePrice : product.price) * quantity;

        try {
            const result = await PortOne.requestPayment({
                storeId: process.env.NEXT_PUBLIC_PORTONE_V2_STORE_ID!,
                channelKey: process.env.NEXT_PUBLIC_PORTONE_V2_CHANNEL_KEY!,
                paymentId: orderUid,
                orderName: product.name,
                totalAmount: amount,
                currency: 'CURRENCY_KRW',
                payMethod: 'CARD',
                customer: {
                    fullName: form.buyerName,
                    email: form.buyerEmail,
                    phoneNumber: form.buyerPhone,
                },
                customData: {
                    productId: product.productId,
                },
            });

            // 정상 결제 시 로직 (예: 백엔드 결제 확인 요청 등)
            console.log('일단 뭐든 성공', result);
        } catch (err: any) {
            toast.error(err?.message || '결제 요청 중 오류 발생');
        }
    };

    return (
        <main className="min-h-screen flex flex-col items-center justify-center px-4 py-8 bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
            <Toaster />

            <div className="w-full max-w-md mb-8">
                <h2 className="text-lg font-semibold mb-3">주문 상품</h2>
                {loading ? (
                    <div className="text-text-secondary dark:text-dark-text-secondary">상품 정보를 불러오는 중...</div>
                ) : (
                    product && (
                        <ProductLineItemCard
                            item={product}
                            onQuantityChange={handleQuantityChange}
                        />
                    )
                )}
            </div>

            <form
                noValidate
                onSubmit={handleSubmit}
                className="w-full max-w-md p-8 bg-surface dark:bg-dark-surface rounded-lg shadow-md"
            >
                <h1 className="text-2xl font-bold mb-6 text-center">비회원 결제</h1>

                <div className="mb-8">
                    <h2 className="text-lg font-semibold mb-2">구매자 정보</h2>
                    <div className="border border-border dark:border-dark-border rounded-md overflow-hidden">
                        <input name="buyerName" placeholder="이름" value={form.buyerName ?? ''} onChange={handleChange} required className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent" />
                        <input name="buyerEmail" type="email" placeholder="이메일" value={form.buyerEmail ?? ''} onChange={handleChange} required className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent" />
                        <input name="buyerPhone" placeholder="전화번호" value={form.buyerPhone ?? ''} onChange={handleChange} required className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent" />
                        <input name="buyerAddress" placeholder="주소" value={form.buyerAddress ?? ''} onChange={handleChange} required className="w-full px-4 py-3 bg-transparent" />
                    </div>
                </div>

                <div className="mb-8">
                    <h2 className="text-lg font-semibold mb-2">수령인 정보</h2>
                    <div className="border border-border dark:border-dark-border rounded-md overflow-hidden">
                        <input name="recipientName" placeholder="이름" value={form.recipientName ?? ''} onChange={handleChange} required className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent" />
                        <input name="recipientPhone" placeholder="전화번호" value={form.recipientPhone ?? ''} onChange={handleChange} required className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent" />
                        <input name="recipientAddress" placeholder="주소" value={form.recipientAddress ?? ''} onChange={handleChange} required className="w-full px-4 py-3 bg-transparent" />
                    </div>
                </div>

                <button type="submit" className="w-full py-2 font-bold bg-primary text-text-primary dark:bg-primary dark:text-dark-text-primary rounded hover:opacity-90">
                    결제 진행
                </button>
            </form>
        </main>
    );
}
