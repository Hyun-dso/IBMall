// /components/Footer.tsx
'use client';

import Link from 'next/link';
import Logo from '@/components/ui/Logo';
import {
    CENTER_CONTENT
} from '@/constants/styles';

export default function Footer() {
    return (
        <div className="w-full max-w-screen-xl mx-auto my-auto flex flex-col sm:flex-row items-center justify-between gap-4 text-text-secondary dark:text-dark-text-secondary">
            <div className="flex items-center gap-2">
                <Logo withText={false} />
                <span className="text-sm">© 2025 IBMall. All rights reserved.</span>
            </div>
            <div className="flex gap-4 text-sm">
                <Link
                    href="/privacy"
                    className="hover:text-text-primary dark:hover:text-dark-text-primary transition"
                >
                    개인정보처리방침
                </Link>
                <Link
                    href="/terms"
                    className="hover:text-text-primary dark:hover:text-dark-text-primary transition"
                >
                    이용약관
                </Link>
            </div>
        </div>
    );
}
