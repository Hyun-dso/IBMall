// /components/ui/Logo.tsx
'use client';

import Link from 'next/link';

interface Props {
    withText?: boolean; // 로고 옆에 텍스트 표시 여부
    size?: string;
}

export default function Logo({ withText = false, size }: Props) {
    return (
        <Link
            href="/"
            aria-label="홈으로 이동"
            className='text-[var(--primary)] inline-flex hover:opacity-80 justify-center items-center'
        >
            <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 64 64"
                className={((size === 'lg')) ? `w-8 h-8` : `w-6 h-6`}
                fill="currentColor"
            >
                <path d="M50 48 L34 24 L30 24 L18 16 L14 48 L30 24 L34 24 L46 16" />
            </svg>
            {withText && <span className={((size === 'lg')) ? `font-bold text-2xl` : `font-bold text-lg`}>IBMall</span>}
        </Link>
    );
}
