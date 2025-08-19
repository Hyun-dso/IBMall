// /app/payments/guest/page.tsx (Server Component)
import { redirect } from 'next/navigation';

export default function Page({ searchParams }: { searchParams: { productId?: string } }) {
    const { productId } = searchParams;
    if (productId) {
        redirect(`/payments/guest/single?productId=${productId}`);
    }
    redirect('/payments/guest/cart');
}
