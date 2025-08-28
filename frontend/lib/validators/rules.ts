// /lib/validators/rules.ts
/** ===== Email ===== */
const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export const normalizeEmail = (v: string) => v.trim().toLowerCase();
export const isEmail = (v: string): boolean => EMAIL_RE.test(normalizeEmail(v));

/** ===== Phone (KR) ===== */
/** 한국 번호 정규화: 숫자만 → 국내 표기 */
export function normalizePhone(raw: string): string {
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
  const n = normalizePhone(v);
  return /^(02|0\d{2})-\d{3,4}-\d{4}$/.test(n) || /^(15|16|18)\d{2}-\d{4}$/.test(n);
};

/** ===== Password ===== */
export type PasswordPolicy = {
  min?: number;
  max?: number;
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

export const ensurePostcodeScript = async () => {
  // if ((window as any).daum?.Postcode) return;
  await new Promise<void>((resolve) => {
    const script = document.createElement('script');
    script.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
    script.onload = () => resolve();
    document.body.appendChild(script);
  });
};

/** ===== Phone (KR) – 입력용 포맷터 ===== */

/**
 * 사용자가 타이핑할 때마다 호출해서 보기 좋게 하이픈을 넣어준다.
 * (onChange에서 쓰기 좋음)
 *
 * 규칙:
 * - +82 / 82로 시작하면 국내표기 0으로 치환
 * - 02(서울), 01x(이동통신), 0xx(지역번호), 15/16/18xx(대표번호) 케이스 대응
 * - 자리수에 맞춰 3-4 패턴으로 하이픈 분할
 */
export function formatPhoneInput(raw: string): string {
  if (!raw) return '';

  // +82, 82 → 0으로 치환하고 숫자만 남김
  let d = raw.replace(/[^\d+]/g, '');
  if (d.startsWith('+82')) d = '0' + d.slice(3);
  if (d.startsWith('82')) d = '0' + d.slice(2);
  d = d.replace(/\D/g, '');
  if (!d) return '';

  // 02 (서울)
  if (d.startsWith('02')) {
    if (d.length <= 2) return d;                             // 02
    if (d.length <= 5) return `02-${d.slice(2)}`;            // 02-xxx
    if (d.length <= 9) return `02-${d.slice(2, d.length - 4)}-${d.slice(-4)}`; // 02-xxx-xxxx
    return `02-${d.slice(2, 6)}-${d.slice(6, 10)}`;          // 10자리 제한
  }

  // 이동통신 010/011/016/017/018/019
  if (/^01[016789]/.test(d)) {
    if (d.length <= 3) return d;                             // 010
    if (d.length <= 7) return `${d.slice(0, 3)}-${d.slice(3)}`;                // 010-xxxx
    if (d.length <= 11) return `${d.slice(0, 3)}-${d.slice(3, d.length - 4)}-${d.slice(-4)}`; // 010-xxxx-xxxx
    return `${d.slice(0, 3)}-${d.slice(3, 7)}-${d.slice(7, 11)}`;             // 11자리 제한
  }

  // 대표번호 15xx/16xx/18xx
  if (/^(15|16|18)\d{0,6}$/.test(d)) {
    if (d.length <= 4) return d;                             // 15xx
    return `${d.slice(0, 4)}-${d.slice(4, 8)}`;              // 15xx-xxxx
  }

  // 그 외 지역번호(070/050 포함 0xx)
  if (/^0\d{2}/.test(d)) {
    if (d.length <= 3) return d;                             // 0xx
    if (d.length <= 6) return `${d.slice(0, 3)}-${d.slice(3)}`;                // 0xx-xxx
    if (d.length <= 10) return `${d.slice(0, 3)}-${d.slice(3, d.length - 4)}-${d.slice(-4)}`; // 0xx-xxx-xxxx
    return `${d.slice(0, 3)}-${d.slice(3, 7)}-${d.slice(7, 11)}`;             // 11자리 제한
  }

  // 기타: 마지막 4자리만 하이픈
  return d.length > 4 ? `${d.slice(0, d.length - 4)}-${d.slice(-4)}` : d;
}

/**
 * blur 시(입력 종료) 한 번 더 정리하는 최종 포맷터.
 * 내부적으로 normalizePhone을 사용해 확정 포맷으로 수선.
 */
export function formatPhoneOnBlur(raw: string): string {
  return normalizePhone(raw);
}
