'use client';

import { useEffect } from 'react';

export default function ThemeSync() {
  useEffect(() => {
    const update = () => {
      const theme = localStorage.getItem('theme');
      const systemPrefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      if (theme === 'dark' || (!theme || theme === 'system') && systemPrefersDark) {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }
    };

    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    mediaQuery.addEventListener('change', update);

    update(); // mount 시 초기 적용

    return () => {
      mediaQuery.removeEventListener('change', update);
    };
  }, []);

  return null;
}
