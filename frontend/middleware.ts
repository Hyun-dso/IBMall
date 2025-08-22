// /middleware.ts
import { NextRequest, NextResponse } from 'next/server';

const PROTECTED_PATHS = ['/payments/member', '/mypage'];

export function middleware(request: NextRequest) {
    const { pathname, search } = request.nextUrl;
    const accessToken = request.cookies.get('accessToken')?.value;

    // 회원 전용 경로 보호
    if (PROTECTED_PATHS.some((p) => pathname.startsWith(p))) {
        if (!accessToken) {
            const url = request.nextUrl.clone();
            url.pathname = '/signin';
            url.search = `?redirect=${encodeURIComponent(pathname + search)}`;
            const res = NextResponse.redirect(url);

            // 테마 헤더 유지
            setThemeHeader(request, res);
            return res;
        }
    }

    // 기본 응답
    const res = NextResponse.next();
    setThemeHeader(request, res);
    return res;
}

// 테마 헤더 공통 처리
function setThemeHeader(req: NextRequest, res: NextResponse) {
    const theme = req.cookies.get('theme')?.value || 'system';
    const prefersDark = req.headers.get('sec-ch-prefers-color-scheme') === 'dark';
    const resolved =
        theme === 'dark' || (theme === 'system' && prefersDark) ? 'dark' : 'light';
    res.headers.set('x-theme', resolved);
}

export const config = {
    // 기존 테마용 매처 유지, 보호 경로도 포함됨
    matcher: ['/', '/((?!_next|api|favicon.ico).*)'],
};
