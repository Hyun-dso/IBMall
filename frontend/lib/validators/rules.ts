// /lib/validators/rules.ts

// 길이 상수(스펙 기반)
export const MAX_EMAIL = 255;
export const MAX_NAME = 50;
export const MAX_NICKNAME = 50;
export const MAX_PHONE = 20;
export const MAX_ADDRESS = 255;

// 로그인에서는 형식 검증 불필요했지만,
// 회원가입 명세에 "이메일 형식"이 있으므로 과하지 않은 최소 수준만 적용.
export function isValidEmailFormat(v: string): boolean {
    if (!v) return false;
    if (v.length > MAX_EMAIL) return false;
    // 최소한의 형식: 공백 금지 + '@'와 '.' 포함
    return /^\S+@\S+\.\S+$/.test(v);
}

export function normalizePhone(input: string): string {
    // 숫자만 추출
    const digits = (input || '').replace(/\D+/g, '');
    if (digits.length <= 3) return digits;
    if (digits.length <= 7) return `${digits.slice(0, 3)}-${digits.slice(3)}`;
    if (digits.length === 8) return `${digits.slice(0, 4)}-${digits.slice(4)}`; // 1588-0000 류
    if (digits.length === 9) return `${digits.slice(0, 2)}-${digits.slice(2, 5)}-${digits.slice(5)}`;
    if (digits.length === 10) {
        // 지역번호(02) 또는 010/011/070 등
        if (digits.startsWith('02')) {
            return `02-${digits.slice(2, 6)}-${digits.slice(6)}`;
        }
        return `${digits.slice(0, 3)}-${digits.slice(3, 6)}-${digits.slice(6)}`;
    }
    // 11자리(010-1234-5678)
    return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(7, 11)}`;
}

export function isValidPhoneLength(v: string): boolean {
    // 하이픈 제거 후 길이 확인
    const digits = (v || '').replace(/\D+/g, '');
    return digits.length <= MAX_PHONE;
}

export function joinAddress(addr1: string, addr2: string): string {
    return `${(addr1 || '').trim()} ${(addr2 || '').trim()}`.trim();
}

export function isValidAddressLength(addressJoined: string): boolean {
    return addressJoined.length <= MAX_ADDRESS;
}
