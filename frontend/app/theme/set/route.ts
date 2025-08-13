// /app/theme/set/route.ts
import { NextResponse } from 'next/server';
export async function POST(req: Request) {
    const { theme } = await req.json().catch(() => ({ theme: 'system' }));
    const value = theme === 'dark' ? 'dark' : theme === 'light' ? 'light' : 'system';
    const res = NextResponse.json({ ok: true, theme: value });
    res.cookies.set('theme', value, {
        path: '/',
        httpOnly: true,
        sameSite: 'lax',
        secure: process.env.NODE_ENV === 'production',
        maxAge: 60 * 60 * 24 * 365,
    });
    return res;
}
