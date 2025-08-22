// /app/not-found.tsx
'use client';

import Button from "@/components/ui/Button";
import Link from 'next/link';

export default function NotFoundPage() {
    return (
        <main className="min-h-screen flex flex-col items-center justify-center bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
            <h1 className="text-4xl font-bold mb-4">404 - 페이지를 찾을 수 없습니다</h1>
            <p className="text-text-secondary dark:text-dark-text-secondary mb-8">
                보여드릴 게 없어요.. 그러니까.. 이상하게 접속하셨어요.. <br /> 어쩌면 운영자가 링크를 잘못했을 수도 있고요..
            </p >
            <div className="flex items-center gap-3">
                <Button variant="solid" size="md" asChild>
                    <Link href="/">메인으로</Link>
                </Button>
                <Button
                    variant="outline"
                    size="md"
                    onClick={() => {
                        if (typeof window !== 'undefined' && window.history.length > 1) {
                            window.history.back();
                        } else {
                            window.location.href = '/';
                        }
                    }}
                >
                    이전 페이지
                </Button>
            </div>
        </main >
    );
}
