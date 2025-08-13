// /lib/transform.ts
export function emptyToNull(v: string | undefined | null): string | null {
    const s = (v ?? '').trim();
    return s.length === 0 ? null : s;
}
