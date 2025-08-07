// /components/Footer.tsx
'use client';

import Link from 'next/link';
import Logo from '@/components/ui/Logo';

export default function Footer() {
    return (
        <footer className="w-full border-t border-border dark:border-dark-border bg-surface dark:bg-dark-surface text-text-secondary dark:text-dark-text-secondary">
            <div className="max-w-screen-xl mx-auto px-6 py-8 flex flex-col sm:flex-row items-center justify-between gap-4">
                <div className="flex items-center gap-2">
                    <Logo />
                    <span className="text-sm">© 2025 IBMall. All rights reserved.</span>
                </div>
                <div className="flex gap-4 text-sm">
                    <Link href="/privacy" className="hover:text-text-primary dark:hover:text-dark-text-primary transition">
                        개인정보처리방침
                    </Link>
                    <Link href="/terms" className="hover:text-text-primary dark:hover:text-dark-text-primary transition">
                        이용약관
                    </Link>
                </div>
            </div>
        </footer>
    );
}
