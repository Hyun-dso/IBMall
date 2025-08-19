// /components/seller/SellerNav.tsx
'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import Button from '@/components/ui/Button';

const NAV = [
    { href: '/seller/dashboard', label: '대시보드' },
    { href: '/seller/products', label: '상품' },
    { href: '/seller/orders', label: '주문' },
    { href: '/seller/categories', label: '카테고리' },
];

export default function SellerNav() {
    const pathname = usePathname();

    return (
        <nav className="p-3">
            <ul className="space-y-1">
                {NAV.map((i) => {
                    const active = pathname === i.href || pathname.startsWith(i.href + '/');
                    const variant = active ? 'solid' : 'outline';

                    return (
                        <li key={i.href}>
                            <Button
                                asChild
                                variant={variant}
                                size="md"
                                className="w-full justify-start rounded-lg border border-border dark:border-dark-border"
                            >
                                <Link href={i.href} aria-current={active ? 'page' : undefined}>
                                    {i.label}
                                </Link>
                            </Button>
                        </li>
                    );
                })}
            </ul>
        </nav>
    );
}
