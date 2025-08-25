// /lib/validators/rules.ts
/** ===== Email ===== */
const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export const normalizeEmail = (v: string) => v.trim().toLowerCase();
export const isEmail = (v: string): boolean => EMAIL_RE.test(normalizeEmail(v));

/** ===== Phone (KR) ===== */
/** 한국 번호 정규화: 숫자만 → 국내 표기 */
export function normalizePhoneKR(raw: string): string {
    let d = (raw || '').replace(/\D+/g, '');
    if (!d) return '';
    if (d.startsWith('82')) d = '0' + d.slice(2); // +82 → 0

    // 서울 02
    if (d.startsWith('02')) {
        if (d.length === 9) return `02-${d.slice(2, 5)}-${d.slice(5)}`;   // 02-xxx-xxxx
        if (d.length === 10) return `02-${d.slice(2, 6)}-${d.slice(6)}`;   // 02-xxxx-xxxx
        return `02-${d.slice(2)}`;
    }

    // 이동통신 010/011/016/017/018/019
    if (/^01[016789]/.test(d)) {
        if (d.length === 10) return `${d.slice(0, 3)}-${d.slice(3, 6)}-${d.slice(6)}`;
        if (d.length === 11) return `${d.slice(0, 3)}-${d.slice(3, 7)}-${d.slice(7)}`;
    }

    // 대표번호(15xx/16xx/18xx-xxxx)
    if (/^(15|16|18)\d{6}$/.test(d)) return `${d.slice(0, 4)}-${d.slice(4)}`;

    // 그 외 지역번호 대략 처리
    if (/^0\d{2,3}/.test(d)) {
        if (d.length === 10) return `${d.slice(0, 3)}-${d.slice(3, 6)}-${d.slice(6)}`;
        if (d.length === 11) return `${d.slice(0, 3)}-${d.slice(3, 7)}-${d.slice(7)}`;
    }

    return d;
}

export const isPhone = (v: string): boolean => {
    const n = normalizePhoneKR(v);
    return /^(02|0\d{2})-\d{3,4}-\d{4}$/.test(n) || /^(15|16|18)\d{2}-\d{4}$/.test(n);
};

/** ===== Password ===== */
export type PasswordPolicy = {
    min?: number; max?: number;
    requireLetter?: boolean;
    requireNumber?: boolean;
    requireSpecial?: boolean;
    disallowWhitespaceEdge?: boolean;
};

const DEFAULT_POLICY: Required<PasswordPolicy> = {
    min: 8, max: 64,
    requireLetter: true,
    requireNumber: true,
    requireSpecial: false,
    disallowWhitespaceEdge: true,
};

/** 정책 충족 여부만 boolean으로 반환 */
export function isPasswordValid(pwRaw: string, policy: PasswordPolicy = {}): boolean {
    const p = { ...DEFAULT_POLICY, ...policy };
    if (!pwRaw) return false;
    if (p.disallowWhitespaceEdge && pwRaw.trim() !== pwRaw) return false;

    const pw = pwRaw;
    if (pw.length < p.min || pw.length > p.max) return false;
    if (p.requireLetter && !/[A-Za-z]/.test(pw)) return false;
    if (p.requireNumber && !/\d/.test(pw)) return false;
    if (p.requireSpecial && !/[^A-Za-z0-9]/.test(pw)) return false;
    return true;
}

export const isPasswordConfirmed = (pw: string, confirm: string): boolean => pw === confirm;
