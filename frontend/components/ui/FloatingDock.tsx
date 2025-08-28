// /components/ui/FloatingDock.tsx
'use client';

import { useEffect, useState } from 'react';
import FloatingCart from '@/components/FloatingCart';
import ThemeToggle from '@/components/util/ThemeToggle';

const MIN_DESKTOP_WIDTH = 1024;

export default function FloatingDock() {
    const [isDesktop, setIsDesktop] = useState(true);

    useEffect(() => {
        const onResize = () => setIsDesktop(window.innerWidth >= MIN_DESKTOP_WIDTH);
        onResize();
        window.addEventListener('resize', onResize);
        return () => window.removeEventListener('resize', onResize);
    }, []);

    return (
        <div
            className="fixed right-6 bottom-6 z-[1000] flex flex-col items-end gap-2 pointer-events-none"
            style={{ paddingBottom: 'env(safe-area-inset-bottom)' }}
        >
            <div className="pointer-events-auto">
                <ThemeToggle />
            </div>

            {isDesktop && (
                <div className="pointer-events-auto">
                    <FloatingCart />
                </div>
            )}
        </div>
    );
}
