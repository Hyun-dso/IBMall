import ThemeToggle from '@/components/ui/ThemeToggle';
import './globals.css';
import { getThemeFromCookies, systemThemeBootstrapScript } from '@/lib/theme';
import GlobalToast from '@/components/GlobalToast';
import Header from '@/components/Header';
import { getUserFromServer } from '@/lib/api/members.server';
import Footer from '@/components/Footer';

export const dynamic = 'force-dynamic'; // 임시(원인 제거 후 삭제 가능)

export default async function RootLayout({ children }: { children: React.ReactNode }) {
  const theme = await getThemeFromCookies();

  let user = null;
  try {
    user = await getUserFromServer();
  } catch {
    user = null;
  }

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
        <main className="mt-24">{children}</main>
        <Footer />
        <ThemeToggle initial={theme} />
      </body>
    </html>
  );
}
