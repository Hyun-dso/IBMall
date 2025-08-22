// /app/ThemeSync.tsx
'use client';

import { useEffect } from 'react';

export default function ThemeSync() {
  useEffect(() => {
    const applyTheme = () => {
      const theme = localStorage.getItem('theme');
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      const isDark = theme === 'dark' || (theme === 'system' && prefersDark);
      document.documentElement.classList.toggle('dark', isDark);
    };

    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    mediaQuery.addEventListener('change', applyTheme);

    applyTheme(); // mount 시 최초 적용

    return () => {
      mediaQuery.removeEventListener('change', applyTheme);
    };
  }, []);

  return null;
}
