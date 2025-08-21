// /components/layouts/ProductGridLayout.tsx

'use client';

interface ProductGridLayoutProps {
    title?: string;
    children: React.ReactNode;
}

export default function ProductGridLayout({ title, children }: ProductGridLayoutProps) {
    return (
        <section className="max-w-screen-xl mx-auto px-4 pt-24 pb-16">
            {title && (
                <h1 className="text-2xl font-bold mb-8 text-center text-text-primary dark:text-dark-text-primary">
                    {title}
                </h1>
            )}
            <div className="grid gap-6 grid-cols-[repeat(auto-fit,minmax(240px,1fr))]">
                {children}
            </div>
        </section>
    );
}
