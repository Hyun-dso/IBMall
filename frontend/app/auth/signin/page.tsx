// /app/signin/page.tsx
import Form from '@/components/form/SigninForm';

export default async function SigninPage({
    searchParams,
}: {
    searchParams: Promise<{ next?: string }>;
}) {
    const sp = await searchParams;         // ✅ 먼저 await
    return <Form {...(sp.next ? { next: sp.next } : {})} />; // ✅ ...{} 사용
}