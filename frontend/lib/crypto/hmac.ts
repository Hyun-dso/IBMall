// /lib/crypto/hmac.ts
import { createHmac } from 'crypto';

const enc = (b: Buffer) => b.toString('base64').replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/,'');

export function signHmac(payload: object, secret: string) {
  const body = Buffer.from(JSON.stringify(payload));
  const sig = createHmac('sha256', secret).update(body).digest();
  return `${enc(body)}.${enc(sig)}`;
}

export function verifyHmac(token: string, secret: string): { ok: boolean; payload?: any } {
  const [b64, sig] = token.split('.');
  if (!b64 || !sig) return { ok: false };
  const body = Buffer.from(b64.replace(/-/g, '+').replace(/_/g, '/'), 'base64');
  const expected = createHmac('sha256', secret).update(body).digest();
  const got = Buffer.from(sig.replace(/-/g, '+').replace(/_/g, '/'), 'base64');
  if (expected.length !== got.length) return { ok: false };
  let same = 0;
  for (let i = 0; i < expected.length; i++) same |= expected[i]! ^ got[i]!;
  if (same !== 0) return { ok: false };
  try {
    return { ok: true, payload: JSON.parse(body.toString('utf8')) };
  } catch {
    return { ok: false };
  }
}
