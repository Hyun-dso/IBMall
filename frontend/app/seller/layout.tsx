// /app/seller/layout.tsx
import { requireRole } from '@/lib/auth/role.server';
import SellerNav from '@/components/seller/SellerNav';

export default async function SellerLayout({ children }: { children: React.ReactNode }) {
    await requireRole('SELLER', '/signin');

    return (
        <div className="w-full mx-auto max-w-screen-xl min-h-dvh bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
            <div className="px-6 pb-10 grid grid-cols-1 md:grid-cols-[240px_1fr] gap-6">
                <div className="border border-border dark:border-dark-border rounded-xl bg-surface dark:bg-dark-surface">
                    <SellerNav />
                </div>
                <section className="max-w-screen-xl mx-auto min-h-[40vh]">{children}</section>
            </div>
        </div>
    );
}
