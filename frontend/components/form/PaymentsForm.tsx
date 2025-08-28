import { useMemo, useState, useReducer } from 'react';
import { useRouter } from 'next/navigation';
import { useCartStore } from '@/stores/useCartStore';
import CartPanel from '@/components/cart/CartPanel';
import { showToast } from '@/lib/toast';
import { formatPhoneInput, formatPhoneOnBlur, isEmail, isPhone, normalizePhone } from '@/lib/validators/rules';
import { INPUT_GROUP_CLASS, INPUT_CLASS, INPUT_DIVIDER_CLASS, FORM_CLASS, CENTER_CONTENT } from '@/app/constants/styles';
import { useDaumPostcode } from '@/hooks/useDaumPostcode';
import { GuestCheckoutInput, Payment1Request } from '@/types/payment';
import ProductLineItemCard from '../product/ProductLineItemCard';
import { ProductLineItem } from '@/types/cart';

type Props = {
    title: string;
    submitLabel: string;
    onSubmit: (payload: GuestCheckoutInput) => Promise<void>;
    initial?: Partial<GuestCheckoutInput>;
};

const CARD_CLASS = 'border border-[var(--border)] rounded-lg bg-[var(--surface)] shadow-sm';

const currency = new Intl.NumberFormat('ko-KR');

export default function CartPaymentFrom({
    initial,
    title,
    submitLabel,
    onSubmit,
}: Props) {
    const group = 'payment';

    const items = useCartStore((s) => s.items);
    const updateQty = useCartStore((s) => s.updateQty);
    const remove = useCartStore((s) => s.remove);

    const [submitting, setSubmitting] = useState(false);


    const [form, setForm] = useReducer(
        (s: FormState, p: Partial<FormState>) => ({ ...s, ...p }),
        {
            email: initial?.email ?? '',
            name: initial?.name ?? '',
            phone: initial?.phone ?? '',
            sendToOther: false,
            address1: initial?.address1 ?? '',
            address2: initial?.address2 ?? '',
            recipientName: '',
            recipientPhone: '',
        }
    );

    const { search: openPostcode, detailRef } = useDaumPostcode();

    const total = useMemo(
        () =>
            items.reduce((acc, cur) => {
                const unit = cur.isTimeSale && cur.timeSalePrice != null ? cur.timeSalePrice : cur.price;
                return acc + unit * cur.quantity;
            }, 0),
        [items]
    );

    function validate(): string | null {
        if (!form.email.trim()) return '이메일을 입력해야 해요';
        if (!isEmail(form.email)) return '이메일 형식이 올바르지 않아요';
        if (!form.name.trim()) return '이름을 입력해야 해요';
        if (form.phone && !isPhone(form.phone)) return '연락처 형식이 올바르지 않아요';
        // 주소 필수 규칙이 있으면 활성화:
        if (!form.address1.trim()) return '주소를 입력하세요.';
        if (!form.address2.trim()) return '상세 주소를 입력하세요.';
        return null;
    }


    type FormState = GuestCheckoutInput;

    function buildPayload(form: FormState): GuestCheckoutInput {
        return {
            name: form.name.trim(),
            email: form.email.trim(),
            phone: form.phone.trim(),              // 서버 보내기 직전에 normalize
            address1: form.address1.trim(),
            address2: form.address2.trim(),
            sendToOther: form.sendToOther,
            recipientName: (form.sendToOther ? form.recipientName : form.name).trim(),
            recipientPhone: (form.sendToOther ? form.recipientPhone : form.phone).trim(),
        };
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (submitting) return;

        const msg = validate();
        if (msg) {
            showToast.error(msg, { group });
            return;
        }

        try {
            setSubmitting(true);

            await onSubmit(buildPayload(form));
        } catch {
            showToast.error('요청 처리 중 오류가 발생했어요', { group });
        } finally {
            setSubmitting(false);
        }
    };

    const [product, setProduct] = useState<ProductLineItem | null>(null);
    return (
        <form
            noValidate
            className={`${FORM_CLASS} ${CENTER_CONTENT} flex flex-col justify-center items-center`}
            onSubmit={handleSubmit}
        >
            <h2 className="text-lg font-semibold">{title}</h2>


            <ProductLineItemCard item={product} onQuantityChange={handleQuantityChange} />

            {/* 장바구니 섹션: 리스트만 카드로 감싸서 렌더링 */}
            <section className={`p-6 ${CARD_CLASS}`}>
                <h2 className="text-lg font-semibold mb-4">장바구니</h2>
                {items.length === 0 ? (
                    <div className="text-[var(--foreground-secondary)]">담긴 상품이 없습니다.</div>
                ) : (
                    <CartPanel
                        items={items}
                        onQtyChange={(pid, qty, optId) => updateQty(pid, qty, optId ?? null)}
                        onRemove={(pid, optId) => remove(pid, optId ?? null)}
                        className="space-y-2"
                    />
                )}
            </section>

            {/* 주문/배송 및 결제 섹션 */}
            <h2 className="text-lg font-semibold mt-16 mb-4">주문/배송 정보</h2>

            <h3 className="text-start w-full text-base font-semibold mb-2">구매자 정보</h3>
            <div className={`mb-6 ${INPUT_GROUP_CLASS}`}>
                <input
                    name="buyerName"
                    placeholder="이름"
                    value={form.name}
                    onChange={(e) => setForm({ name: e.currentTarget.value })}
                    required
                    className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                />
                <input
                    name="buyerEmail"
                    type="email"
                    placeholder="이메일"
                    value={form.email}
                    onChange={(e) => setForm({ email: e.currentTarget.value })}
                    required
                    className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                />
                <input
                    className={INPUT_CLASS}
                    name="buyerPhone"
                    placeholder="연락처"
                    value={form.phone}
                    onChange={(e) => setForm({ phone: formatPhoneInput(e.currentTarget.value) })}
                    onBlur={(e) => setForm({ phone: formatPhoneOnBlur(e.currentTarget.value) })}
                    required
                    autoComplete="tel"
                    inputMode="numeric"
                />
            </div>

            <div className="flex w-full items-center justify-between mb-2">
                <h3 className="text-base font-semibold">배송지</h3>
                <Switch
                    checked={form.sendToOther}
                    onChange={(e) => setForm({ sendToOther: e })}
                    label="다른 사람에게 보내기"
                />
            </div>

            <div className={`mb-6 ${INPUT_GROUP_CLASS}`}>
                <input
                    name="address1"
                    placeholder="주소"
                    value={form.address1}
                    readOnly
                    onClick={() => openPostcode((addr) => setForm({ address1: addr }))}
                    onFocus={() => openPostcode((addr) => setForm({ address1: addr }))}
                    required
                    className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS} cursor-pointer`}
                />
                <input
                    name="address2"
                    placeholder="상세주소"
                    value={form.address2}
                    ref={detailRef}
                    onChange={(e) => setForm({ address2: e.currentTarget.value })}
                    required
                    className={`${INPUT_CLASS} ${form.sendToOther ? ` ${INPUT_DIVIDER_CLASS}` : ''}`}
                />
                {form.sendToOther && (
                    <>
                        <input
                            name="recipientName"
                            placeholder="수령인 이름"
                            value={form.recipientName ?? ''}
                            onChange={(e) => setForm({ recipientName: e.currentTarget.value })}
                            required
                            className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                        />
                        <input
                            name="recipientPhone"
                            placeholder="수령인 연락처"
                            value={form.recipientPhone ?? ''}
                            onChange={(e) => setForm({ recipientPhone: e.currentTarget.value })}
                            required
                            className={INPUT_CLASS}
                        />
                    </>
                )}
            </div>

            <div className="mb-4 w-full flex items-center justify-between">
                <span className="text-[var(--foreground-secondary)] ">결제 금액</span>
                <span className="text-xl font-semibold">{currency.format(total)}원</span>
            </div>

            <button
                type='submit'
                className="w-full py-3 font-bold border border-[var(--primary)] rounded-md"
                disabled={submitting || !items.length}
            >
                {submitting ? '처리 중...' : submitLabel}
            </button>
        </form>
    )
}

/* 내부 컴포넌트: 토글 스위치 */
function Switch({
    checked,
    onChange,
    label,
}: {
    checked: boolean;
    onChange: (next: boolean) => void;
    label: string;
}) {
    return (
        <button
            type="button"
            role="switch"
            aria-checked={checked}
            onClick={() => onChange(!checked)}
            className="flex justify-end items-center gap-2 hover:cursor-pointer"
        >
            <span className="text-sm text-[var(--foreground-secondary)]">{label}</span>
            <span
                className={[
                    'inline-flex h-6 w-11 items-center rounded-full transition',
                    checked ? 'bg-[var(--primary)]' : 'bg-[var(--border)]',
                ].join(' ')}
            >
                <span
                    className={[
                        'inline-block h-5 w-5 rounded-full bg-[var(--background)] transform transition',
                        checked ? 'translate-x-5' : 'translate-x-1',
                    ].join(' ')}
                />
            </span>
        </button>
    );
}
