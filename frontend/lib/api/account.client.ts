// /lib/api/account.client.ts
import { isEmail, normalizeEmail } from '@/lib/validators/rules';

export type SendEmailCodeResult =
    | { ok: true; status: 200 }                                    // 전송 성공
    | { ok: false; reason: 'invalid'; status: 400 }                 // 형식 오류
    | { ok: false; reason: 'taken'; status: 409 }                   // 이미 가입된 이메일
    | { ok: false; reason: 'cooldown'; status: 429; retryAfter: number } // 60초 내 재요청
    | { ok: false; reason: 'error'; status: number };

export async function sendSignupEmailCode(
    email: string,
    signal?: AbortSignal
): Promise<SendEmailCodeResult> {
    if (!isEmail(email)) return { ok: false, reason: 'invalid', status: 400 };

    const res = await fetch('/api/email/code/send', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ email: normalizeEmail(email) }),
        signal,
    });

    if (res.status === 200) return { ok: true, status: 200 };
    if (res.status === 409) return { ok: false, reason: 'taken', status: 409 };
    if (res.status === 429) {
        const ra = res.headers.get('Retry-After');
        const retryAfter = ra ? parseInt(ra, 10) || 60 : 60;
        return { ok: false, reason: 'cooldown', status: 429, retryAfter };
    }
    if (res.status === 400) return { ok: false, reason: 'invalid', status: 400 };
    return { ok: false, reason: 'error', status: res.status };
}
