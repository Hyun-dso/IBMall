// /app/error.tsx
'use client';

import { useEffect, useMemo } from 'react';
import Link from 'next/link';
import Button from '@/components/ui/Button';
import type { AppError } from '@/types/errors';

export default function GlobalError({
  error,
  reset,
}: {
  error: AppError;
  reset: () => void;
}) {
  useEffect(() => {
    console.error('[GlobalError]', {
      message: error?.message,
      stack: error?.stack,
      digest: error?.digest,
    });
  }, [error]);

  const safeId = useMemo(() => error?.digest ?? 'unknown', [error]);
  const isDev = process.env.NODE_ENV !== 'production';

  return (
    <main className="min-h-screen flex flex-col items-center justify-center gap-6 bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary px-6">
      <header className="text-center">
        <h1 className="text-4xl font-bold">서버 오류가 발생했어요</h1>
        <p className="mt-2 text-text-secondary dark:text-dark-text-secondary">
          손님 탓은 아니에요.. 그러니까 운영자 탓이라는 거죠..
        </p>
      </header>

      <div className="flex items-center gap-3">
        <Button variant="solid" size="md" onClick={reset}>
          다시 시도
        </Button>
        <Button variant="outline" size="md" asChild>
          <Link href="/">메인으로</Link>
        </Button>
      </div>

      <section className="mt-4 text-sm border border-border dark:border-dark-border rounded-2xl bg-surface dark:bg-dark-surface px-4 py-3 max-w-2xl w-full">
        <div className="flex items-center justify-between">
          <span className="font-medium">오류 식별자</span>
          <code className="text-text-secondary dark:text-dark-text-secondary">{safeId}</code>
        </div>

        {isDev && (
          <details className="mt-3">
            <summary className="cursor-pointer">개발 정보</summary>
            <pre className="mt-2 whitespace-pre-wrap break-words text-sm">
              {error?.message || 'no message'}
              {'\n'}
              {error?.stack || 'no stack'}
            </pre>
          </details>
        )}
      </section>
    </main>
  );
}
