// /components/seller/SellerNav.tsx
'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

const NAV = [
    { href: '/seller/dashboard', label: '대시보드' },
    { href: '/seller/orders', label: '주문' },
    { href: '/seller/categories', label: '카테고리' },
    { href: '/seller/products', label: '상품' },
];

export default function SellerNav() {
    const pathname = usePathname();
    return (
        <nav className="p-3">
            <ul className="space-y-1">
                {NAV.map((i) => {
                    const active = pathname === i.href || pathname.startsWith(i.href + '/');
                    return (
                        <li key={i.href}>
                            <Link
                                href={i.href}
                                className={[
                                    'block w-full px-3 py-2 rounded-lg border',
                                    active
                                        ? 'bg-primary text-text-primary border-border'
                                        : 'bg-transparent text-text-primary border-border dark:border-dark-border hover:bg-surface dark:hover:bg-dark-surface',
                                ].join(' ')}
                            >
                                {i.label}
                            </Link>
                        </li>
                    );
                })}
            </ul>
        </nav>
    );
}
