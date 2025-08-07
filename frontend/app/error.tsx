'use client';

import { useEffect } from 'react';

export default function GlobalError({ error, reset }: { error: Error; reset: () => void }) {
  useEffect(() => {
    console.error(error);
  }, [error]);

  return (
    <main className="min-h-screen flex flex-col items-center justify-center bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
      <h1 className="text-4xl font-bold mb-4">500 - 서버 오류 발생</h1>
      <p className="text-text-secondary dark:text-dark-text-secondary mb-8">
        손님 탓은 아니에요.. 그러니까 운영자 탓이라는 거죠..
      </p>
      <button
        onClick={reset}
        className="px-6 py-3 bg-primary text-white rounded hover:bg-accent transition"
      >
        다시 시도
      </button>
    </main>
  );
}
