// /app/layout.tsx
import '@/styles/globals.css';
import { Toaster } from 'react-hot-toast';
import { headers } from 'next/headers';
import localFont from 'next/font/local';
import Header from '@/components/Header';
import Footer from '@/components/Footer';
import ThemeToggle from '@/components/ui/ThemeToggle';
import ThemeListener from './ThemeSync';
import { getUserFromSession } from '@/lib/auth';

const myFont = localFont({
  src: '../public/fonts/PretendardVariable.woff2',
  display: 'swap',
  variable: '--font-myfont',
});

export const metadata = {
  title: 'IBMall',
  description: '쇼핑몰',
};

export const dynamic = 'force-dynamic'; // SSR 강제 적용

function getInitColorSchemeScript() {
  return `
    (function() {
      try {
        const theme = localStorage.getItem('theme');
        const systemPrefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        if (theme === 'dark' || (!theme || theme === 'system') && systemPrefersDark) {
          document.documentElement.classList.add('dark');
        } else {
          document.documentElement.classList.remove('dark');
        }
      } catch (_) {}
    })();
  `;
}

export default async function RootLayout({ children }: { children: React.ReactNode }) {
  const headerList = await headers();
  const cookie = headerList.get('cookie') || '';
  const user = await getUserFromSession(cookie); // SSR 로그인 상태 조회

  return (
    <html lang="ko" suppressHydrationWarning>
      <head>
        <script dangerouslySetInnerHTML={{ __html: getInitColorSchemeScript() }} />
      </head>
      <body className={`${myFont.variable} font-sans`}>
        <ThemeListener />
        <Header user={user} />
        {children}
        <Footer />
        <Toaster
          toastOptions={{
            style: {
              background: '#E5E5E5',
              color: '#000000',
              border: '1px solid #bbbbbbff',
            },
            success: {
              style: { background: '#16A34A', color: '#ffffff' },
            },
            error: {
              style: { background: '#DC2626', color: '#ffffff' },
            },
          }}
        />
        <ThemeToggle />
      </body>
    </html>
  );
}
