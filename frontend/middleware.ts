// middleware.ts
import { NextRequest, NextResponse } from 'next/server';

export function middleware(request: NextRequest) {
    const theme = request.cookies.get('theme')?.value || 'system';
    const prefersDark = request.headers.get('sec-ch-prefers-color-scheme') === 'dark';

    const resolvedTheme =
        theme === 'dark' || (theme === 'system' && prefersDark) ? 'dark' : 'light';

    const response = NextResponse.next();
    response.headers.set('x-theme', resolvedTheme);

    return response;
}

export const config = {
    matcher: ['/', '/((?!_next|api|favicon.ico).*)'], // 적용할 경로
};
