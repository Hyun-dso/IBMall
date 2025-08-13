// /lib/theme.ts
import { cookies } from 'next/headers';
import type { Theme } from '@/types/common';

export async function getThemeFromCookies(): Promise<Theme> {
    const jar = await cookies();                  // Promise
    const v = jar.get('theme')?.value;
    return v === 'dark' ? 'dark' : v === 'light' ? 'light' : 'system';
}

export function systemThemeBootstrapScript(): string {
    return `(function(){try{var m=window.matchMedia('(prefers-color-scheme: dark)').matches;if(m)document.documentElement.classList.add('dark');}catch(e){}})();`;
}
