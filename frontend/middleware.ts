// /middleware.ts
import { NextResponse, type NextRequest } from 'next/server';
import { ACCESS_COOKIE } from '@/constants/auth';

const GUEST_ONLY = new Set(['/signin', '/signup']);
const PROTECTED_PREFIXES = ['/account', '/orders', '/cart'];

function isProtectedPath(pathname: string) {
    return PROTECTED_PREFIXES.some((p) => pathname.startsWith(p));
}

export function middleware(req: NextRequest) {
    const { pathname, search } = req.nextUrl;
    const hasAuth = Boolean(req.cookies.get(ACCESS_COOKIE)?.value);

    if (hasAuth && GUEST_ONLY.has(pathname)) {
        const url = req.nextUrl.clone();
        url.pathname = '/';
        return NextResponse.redirect(url);
    }

    if (!hasAuth && isProtectedPath(pathname)) {
        const url = req.nextUrl.clone();
        url.pathname = '/signin';
        url.search = search
            ? `${search}&redirect=${encodeURIComponent(pathname)}`
            : `?redirect=${encodeURIComponent(pathname)}`;
        return NextResponse.redirect(url);
    }

    return NextResponse.next();
}

export const config = {
    matcher: ['/((?!_next|.*\\..*|api).*)'],
};
