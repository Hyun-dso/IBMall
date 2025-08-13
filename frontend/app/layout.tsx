// /app/layout.tsx
import ThemeToggle from '@/components/ui/ThemeToggle';
import './globals.css';
import { getThemeFromCookies, systemThemeBootstrapScript } from '@/lib/theme';
import GlobalToast from '@/components/GlobalToast';
import Header from '@/components/Header';
import { getUserFromServer } from '@/lib/api/members.server';
import Footer from '@/components/Footer';
import { Main } from 'next/document';

export default async function RootLayout({ children }: { children: React.ReactNode }) {
  const theme = await getThemeFromCookies();   // ← 반드시 await
  const user = await getUserFromServer(); // SSR로 로그인 상태 획득

  return (
    <html lang="ko" className={theme === 'dark' ? 'dark' : ''} suppressHydrationWarning>
      <head>
        {theme === 'system' && (
          <script dangerouslySetInnerHTML={{ __html: systemThemeBootstrapScript() }} />
        )}
      </head>
      <body>
        <GlobalToast />
        <Header user={user} />
        <main className='mt-24'>
          {children}
        </main>
        <Footer />
        <ThemeToggle initial={theme} />
      </body>
    </html>
  );
}
