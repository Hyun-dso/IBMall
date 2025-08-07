'use client';

import Link from 'next/link';

export default function Logo() {
    return (
        <Link href="/" className="flex items-center gap-2 text-primary hover:text-accent transition">
            <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 64 64"
                width="24"
                height="24"
                fill="currentColor"
            >
                <path d="M50 48 L34 24 L30 24 L18 16 L14 48 L30 24 L34 24 L46 16" />
            </svg>
        </Link>
    );
}
