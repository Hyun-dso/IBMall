// /app/payments/[role]/layout.tsx
import Nav from '@/components/ui/Nav';
import { getUserFromServer } from '@/lib/api/account.server';
import { headers } from 'next/headers';

export default async function MypageLayout({
    children,
}: { children: React.ReactNode;}) {
    const cookie = (await headers()).get('cookie') || '';
    const user = await getUserFromServer(cookie);

    return (
        <div className="w-full mt-4 mx-auto max-w-screen-xl min-h-dvh">
            <div className="px-6 grid grid-cols-1 md:grid-cols-[240px_1fr]">
                <div className="border border-white rounded-md h-fit">
                    <Nav role={user ? user.role : null} />
                </div>
                <section className=" text-text-primary dark:text-dark-text-primary">
                    {children}
                </section>
            </div>
        </div>
    );
}
