// /components/GlobalToast.tsx
'use client';

import { useEffect } from 'react';
import dynamic from 'next/dynamic';
import { Toaster } from 'react-hot-toast';
import { showToast } from '@/lib/toast';

function Inner() {
    useEffect(() => {
        showToast.flushQueued(); // 강한 전환 후 큐에 있던 토스트 재표시
    }, []);

    return (
        <Toaster
            position='bottom-left'
            reverseOrder={false}
            gutter={8}
            toastOptions={{
                style: {
                    background: 'var(--background)',
                    color: 'var(--foreground)',
                    border: '1px solid var(--primary)',
                },
                success: {
                    style: {
                        border: '1px solid var(--success)',
                    }
                },
                error: {
                    style: {
                        border: '1px solid var(--error)',
                    }
                },
            }}
        />
    );
}

export default dynamic(() => Promise.resolve(Inner), { ssr: false });
