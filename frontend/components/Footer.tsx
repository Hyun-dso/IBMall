// /components/Footer.tsx
'use client';

import Link from 'next/link';
import Logo from '@/components/ui/Logo';

export default function Footer() {
    return (
        <footer className="fixed bottom-0 w-full h-[var(--footer-height)] border-t b-border">
            <div className="max-w-screen-xl mx-auto h-full flex items-center justify-between px-6">
                <div className="flex items-center gap-2">
                    <Logo withText={false} size='sm' />
                    <span className="text-sm">© 2025 IBMall. All rights reserved.</span>
                </div>
                <div className="flex gap-4 text-sm text-foreground justify-center items-center">
                    <Link
                        href="/privacy"
                        className="transition hover:cursor-pointer hover:opecity-80"
                    >
                        개인정보처리방침
                    </Link>
                    <Link
                        href="/terms"
                        className="transition hover:cursor-pointer hover:opecity-80"
                    >
                        이용약관
                    </Link>
                </div>
            </div>
        </footer >
    );
}
