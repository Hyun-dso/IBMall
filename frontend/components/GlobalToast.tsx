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
            position="top-left"
            reverseOrder={false}
            gutter={8}
            toastOptions={{
                style: {
                    background: 'var(--color-surface)',
                    color: 'var(--color-text-primary)',
                    border: '1px solid var(--color-border)',
                },
                success: { style: { background: 'var(--color-success)', color: '#ffffff' } },
                error: { style: { background: 'var(--color-error)', color: '#ffffff' } },
            }}
        />
    );
}

// 서버 렌더 비활성화 → 레이아웃의 SSR 재평가와 분리, 라우팅/refresh에도 유지
export default dynamic(() => Promise.resolve(Inner), { ssr: false });
