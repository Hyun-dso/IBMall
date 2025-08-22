// /lib/validators/rules.ts
export const isEmail = (v: string) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
export const isStrongPassword = (v: string) => v.length >= 8 && /[0-9]/.test(v) && /[a-zA-Z]/.test(v);
export const isNickname = (v: string) => /^[a-zA-Z0-9가-힣]{2,20}$/.test(v);
export const digitsOnly = (v: string) => v.replace(/\D/g, '');
export const isMobile010 = (v: string) => /^010\d{7,8}$/.test(digitsOnly(v));
export const notBlank = (v: string) => String(v ?? '').trim().length > 0;

export const normalizePhone = (v: string) => {
    const d = digitsOnly(v).slice(0, 11);
    if (d.length < 4) return d;
    if (d.length < 8) return `${d.slice(0, 3)}-${d.slice(3)}`;
    return `${d.slice(0, 3)}-${d.slice(3, 7)}-${d.slice(7)}`;
};
