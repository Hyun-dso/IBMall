'use client';

import { useEffect, useState } from 'react';

export default function ThemeToggle() {
  const [theme, setTheme] = useState<'light' | 'dark' | 'system'>('system');

  useEffect(() => {
    const stored = localStorage.getItem('theme') as 'light' | 'dark' | 'system' | null;
    const initial =
      stored === 'light' || stored === 'dark' || stored === 'system' ? stored : 'system';
    setTheme(initial);
  }, []);

  useEffect(() => {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    const effectiveDark = theme === 'dark' || (theme === 'system' && prefersDark);
    document.documentElement.classList.toggle('dark', effectiveDark);
    localStorage.setItem('theme', theme);
  }, [theme]);

  const options: { label: string; value: 'light' | 'dark' | 'system' }[] = [
    { label: '시스템', value: 'system' },
    { label: '환하게', value: 'light' },
    { label: '어둡게', value: 'dark' },
  ];

  return (
    <div className="fixed bottom-4 right-4 bg-surface dark:bg-dark-surface border border-border dark:border-dark-border rounded-full shadow-md p-2 flex space-x-2 z-50">
      {options.map(({ label, value }) => {
        const isActive = theme === value;

        const baseClass =
          'px-4 py-1.5 text-sm rounded-full transition-colors';

        const activeClass = isActive
          ? 'bg-primary text-black hover:bg-accent'
          : 'bg-transparent text-text-secondary dark:text-dark-text-secondary';

        return (
          <button
            key={value}
            onClick={() => setTheme(value)}
            className={`${baseClass} ${activeClass}`}
          >
            {label}
          </button>
        );
      })}
    </div>
  );
}
