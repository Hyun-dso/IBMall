import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "@/styles/globals.css";
import GlobalToast from '@/components/util/GlobalToast';
import Header from "@/components/Header";
import Footer from "@/components/Footer";
import { headers } from "next/headers";
import { getUserFromServer } from "@/lib/api/account.server";
import FloatingDock from "@/components/ui/FloatingDock";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "IB Mall",
  description: "안녕ㅎㅎ",
};


export default async function RootLayout({ children, }: Readonly<{ children: React.ReactNode; }>) {

  const cookie = (await headers()).get('cookie') || '';
  const user = await getUserFromServer(cookie);

  return (
    <html lang="ko">
      <body
        className={`${geistSans.variable} ${geistMono.variable} antialiased`}
      >
        <Header user={user} />
        <main className={`relative z-10 w-full flex justify-center min-h-[calc(100dvh-var(--header-height))] mt-[var(--header-height)] mb-[var(--footer-height)] bg-background`}>
          {children}
        </main>
        <GlobalToast />
        <FloatingDock />
        <Footer />
      </body>
    </html>
  );
}
