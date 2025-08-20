<<<<<<< HEAD
// /app/layout.tsx
import '@/styles/globals.css';
import { headers } from 'next/headers';
import localFont from 'next/font/local';
=======
import ThemeToggle from '@/components/ui/ThemeToggle';
import './globals.css';
import { getThemeFromCookies, systemThemeBootstrapScript } from '@/lib/theme';
import GlobalToast from '@/components/GlobalToast';
>>>>>>> origin/0819
import Header from '@/components/Header';
import Footer from '@/components/Footer';
<<<<<<< HEAD
import ThemeSync from './ThemeSync';
import ThemeToggle from '@/components/ui/ThemeToggle';
import { getUserFromServer } from '@/lib/auth';
import FloatingDock from '@/components/ui/FloatingDock';
import GlobalToast from '@/components/GlobalToast';

const myFont = localFont({
  src: '../public/fonts/PretendardVariable.woff2',
  display: 'swap',
  variable: '--font-myfont',
});

export const metadata = {
  title: 'IBMall',
  description: '쇼핑몰',
};

export const dynamic = 'force-dynamic';

export default async function RootLayout({ children }: { children: React.ReactNode }) {
  const cookie = (await headers()).get('cookie') || '';
  const user = await getUserFromServer(cookie);
=======

export const dynamic = 'force-dynamic'; // 임시(원인 제거 후 삭제 가능)

export default async function RootLayout({ children }: { children: React.ReactNode }) {
  const theme = await getThemeFromCookies();

  let user = null;
  try {
    user = await getUserFromServer();
  } catch {
    user = null;
  }
>>>>>>> origin/0819

  return (
    <html lang="ko" suppressHydrationWarning>
      <head>
        {/* FOUC 방지용 테마 초기 적용 */}
        <script
          dangerouslySetInnerHTML={{
            __html: `
              (function() {
                try {
                  var theme = localStorage.getItem('theme');
                  var systemDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
                  var isDark = theme === 'dark' || (!theme || theme === 'system') && systemDark;
                  if (isDark) {
                    document.documentElement.classList.add('dark');
                  } else {
                    document.documentElement.classList.remove('dark');
                  }
                } catch(_) {}
              })();
            `,
          }}
        />
      </head>
      <body className={`${myFont.variable} font-sans`}>
        <ThemeSync />
        <GlobalToast />
        <Header user={user} />
<<<<<<< HEAD
        <main className="relative z-10 w-full flex justify-center min-h-[calc(100dvh-6rem)] mt-[var(--header-height)] pt-4 bg-background dark:bg-dark-background">
          {children}
        </main>
        <footer className="fixed w-full h-24 bottom-0 flex border-t border-border dark:border-dark-border bg-surface dark:bg-dark-surface">
          <Footer />
        </footer>
        <FloatingDock />
        <ThemeToggle />
=======
        <main className="mt-24">{children}</main>
        <Footer />
        <ThemeToggle initial={theme} />
>>>>>>> origin/0819
      </body>
    </html>
  );
}
