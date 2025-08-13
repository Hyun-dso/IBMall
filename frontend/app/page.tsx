// /app/page.tsx
'use client';

import { showToast } from '@/lib/toast';

export default function HomePage() {
  return (
    <main className="p-6 flex gap-4">
      <button
        onClick={() => showToast.success('성공 토스트입니다.')}
        className="px-4 py-2 rounded bg-green-500 text-white"
      >
        성공 토스트
      </button>
      <button
        onClick={() => showToast.loading('로딩 토스트입니다.')}
        className="px-4 py-2 rounded bg-blue-500 text-white"
      >
        로딩 토스트
      </button>
      <button
        onClick={() => showToast.error('에러 토스트입니다.')}
        className="px-4 py-2 rounded bg-red-500 text-white"
      >
        에러 토스트
      </button>
    </main>
  );
}
