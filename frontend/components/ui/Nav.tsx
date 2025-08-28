// /components/seller/SellerNav.tsx
'use client';

import { MemberDto } from '@/types/account';
import Link from 'next/link';
import { usePathname } from 'next/navigation';

let NAV;

export default function Nav({ role }: MemberDto) {

    if (role === 'SELLER') {
        NAV = [
            { href: ' ', label: '대시보드' },
            { href: '/account', label: '계정' },
            { href: '/categories', label: '카테고리' },
            { href: '/products', label: '상품' },
        ];
    }
    else if (role === 'USER') {
        NAV = [
            { href: ' ', label: '대시보드' },
            { href: '/account', label: '계정' },
            { href: '/orders', label: '주문' },
        ];
    }
    else {
        NAV = [
            { href: ' ', label: '대시보드' },
            { href: '/account', label: '계정인데 왜 null 부분에 계속 뜨냐' },
        ]
    }

    const pathname = usePathname();
    return (
        <nav className="p-3">
            <ul className="space-y-1">
                {NAV.map((i) => {
                    const active = pathname === i.href || pathname.startsWith('/mypage' + i.href);
                    return (
                        <li key={i.href}>
                            <Link
                                href={'/mypage' + i.href}
                                className={[
                                    'block w-full px-3 py-2 rounded-md border',
                                    active
                                        ? 'border-[var(--primary)]'
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
