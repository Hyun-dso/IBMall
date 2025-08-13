// /types/common.ts
export type Theme = 'system' | 'light' | 'dark';

export function normalizeTheme(v: unknown): Theme {
    if (v === 'dark') return 'dark';
    if (v === 'light') return 'light';
    return 'system';
}
