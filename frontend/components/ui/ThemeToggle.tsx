'use client';

import { useEffect, useState } from 'react';

export default function ThemeToggle() {
  const [theme, setTheme] = useState<'light' | 'dark' | 'system'>('system');

  useEffect(() => {
    const stored = localStorage.getItem('theme') as 'light' | 'dark' | 'system' | null;
    const initial = stored === 'light' || stored === 'dark' || stored === 'system' ? stored : 'system';
    setTheme(initial);
  }, []);

  useEffect(() => {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    const effectiveDark = theme === 'dark' || (theme === 'system' && prefersDark);
    document.documentElement.classList.toggle('dark', effectiveDark);
    localStorage.setItem('theme', theme);
  }, [theme]);

  return (
    <div className="fixed bottom-4 right-4 bg-surface dark:bg-dark-surface border border-border dark:border-dark-border rounded-full shadow-md p-2 flex space-x-2 z-50">
      <button
        onClick={() => setTheme('system')}
        className={`px-3 py-1 rounded-full text-sm ${theme === 'system'
          ? 'bg-accent text-white'
          : 'text-text-secondary dark:text-dark-text-secondary'
          }`}
      >
        시스템
      </button>
      <button
        onClick={() => setTheme('light')}
        className={`px-3 py-1 rounded-full text-sm ${theme === 'light'
          ? 'bg-accent text-white'
          : 'text-text-secondary dark:text-dark-text-secondary'
          }`}
      >
        환하게
      </button>
      <button
        onClick={() => setTheme('dark')}
        className={`px-3 py-1 rounded-full text-sm ${theme === 'dark'
          ? 'bg-accent text-white'
          : 'text-text-secondary dark:text-dark-text-secondary'
          }`}
      >
        어둡게
      </button>
    </div>
  );
}
