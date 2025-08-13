// /components/ui/Logo.tsx
'use client';

import Link from 'next/link';
import { cn } from '@/lib/utils';

interface LogoProps {
    withText?: boolean; // 로고 옆에 텍스트 표시 여부
    className?: string;
}

export default function Logo({ withText = false, className }: LogoProps) {
    return (
        <Link
            href="/"
            className={cn(
                'flex items-center gap-2 text-primary hover:text-accent transition-colors',
                className
            )}
            aria-label="홈으로 이동"
        >
            <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 64 64"
                className="w-6 h-6"
                fill="currentColor"
            >
                <title>IBMall 로고</title>
                <path d="M50 48 L34 24 L30 24 L18 16 L14 48 L30 24 L34 24 L46 16" />
            </svg>
            {withText && <span className="font-bold text-lg">IBMall</span>}
        </Link>
    );
}
