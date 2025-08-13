// /components/ui/ThemeToggle.tsx
'use client';

import { useEffect, useState, useTransition } from 'react';
import { useRouter } from 'next/navigation';
import type { Theme } from '@/types/common';

const OPTIONS: Array<{ label: string; value: Theme }> = [
    { label: '시스템', value: 'system' },
    { label: '환하게', value: 'light' },
    { label: '어둡게', value: 'dark' },
];

export default function ThemeToggle({ initial }: { initial: Theme }) {
    const [selected, setSelected] = useState<Theme>(initial);
    const [pending, start] = useTransition();
    const router = useRouter();

    // DOM(html) 클래스 즉시 적용
    const applyDom = (t: Theme) => {
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        const isDark = t === 'dark' || (t === 'system' && prefersDark);
        document.documentElement.classList.toggle('dark', isDark);
    };

    // 최초 진입 시 SSR 값 반영
    useEffect(() => { applyDom(initial); /* once */ }, [initial]);

    // system 모드일 때 OS 테마 변경 추적
    useEffect(() => {
        if (selected !== 'system') return;
        const mql = window.matchMedia('(prefers-color-scheme: dark)');
        const onChange = (e: MediaQueryListEvent) =>
            document.documentElement.classList.toggle('dark', e.matches);
        mql.addEventListener('change', onChange);
        return () => mql.removeEventListener('change', onChange);
    }, [selected]);

    const apply = (t: Theme) => {
        if (pending || t === selected) return;
        setSelected(t);     // 낙관적 표시
        applyDom(t);        // 즉시 색상 반영

        // 쿠키 갱신 후 서버 컴포넌트만 새로고침(SSR 값 동기화)
        start(async () => {
            await fetch('/theme/set', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({ theme: t }),
            });
            router.refresh();
        });
    };

    const base = 'px-4 py-1.5 text-sm rounded-full transition-colors hover:cursor-pointer';
    const active = 'bg-primary text-text-primary hover:opacity-90';
    const inactive = 'bg-transparent text-text-secondary dark:text-dark-text-secondary';

    return (
        <div className="fixed bottom-4 right-4 z-50 flex space-x-2 rounded-full border border-border dark:border-dark-border bg-surface dark:bg-dark-surface p-2 shadow-md"
            role="radiogroup" aria-label="테마 선택">
            {OPTIONS.map(({ label, value }) => {
                const isActive = selected === value;
                return (
                    <button key={value} type="button" role="radio" aria-checked={isActive}
                        disabled={pending} onClick={() => apply(value)}
                        className={`${base} ${isActive ? active : inactive}`}>
                        {label}
                    </button>
                );
            })}
        </div>
    );
}
