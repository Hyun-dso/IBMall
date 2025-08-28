import Link from 'next/link';
import { redirect } from 'next/navigation';
import { getUserFromServer } from '@/lib/api/account.server';
import { headers } from 'next/headers';

export default async function MyPage() {

    const cookie = (await headers()).get('cookie') || '';
    const me = await getUserFromServer(cookie);
    if (!me) redirect('/auth/signin');

    return (
        <main className="mx-auto w-full max-w-screen-xl px-6 py-10 text-text-primary bg-background dark:bg-dark-background">
            <h1 className="text-2xl font-semibold mb-6">마이페이지</h1>

            <section className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* 프로필 요약 */}
                <div className="rounded-2xl p-5 border border-border bg-surface dark:border-dark-border dark:bg-dark-surface">
                    <h2 className="text-lg font-semibold mb-3">프로필</h2>
                    <dl className="space-y-2 text-sm text-text-secondary dark:text-dark-text-secondary">
                        <div className="flex justify-between">
                            <dt>이메일</dt>
                            <dd className="text-text-primary dark:text-dark-text-primary">{me.email ?? '-'}</dd>
                        </div>
                        <div className="flex justify-between">
                            <dt>등급</dt>
                            <dd className="text-text-primary dark:text-dark-text-primary">{me.grade ?? '-'}</dd>
                        </div>
                        <div className="flex justify-between">
                            <dt>인증</dt>
                            <dd className="text-text-primary dark:text-dark-text-primary">{me.verified ? '인증' : '미인증'}</dd>
                        </div>
                        <div className="flex justify-between">
                            <dt>가입일</dt>
                            <dd className="text-text-primary dark:text-dark-text-primary">{me.createdAt ?? '-'}</dd>
                        </div>
                    </dl>
                    <div className="mt-4 flex gap-2">
                        <button>
                            <Link href="/mypage/profile">프로필 수정</Link>
                        </button>
                        <button>
                            <Link href="/auth/signout">로그아웃</Link>
                        </button>
                    </div>
                </div>

                {/* 주문/배송 진입 */}
                <div className="rounded-2xl p-5 border border-border bg-surface dark:border-dark-border dark:bg-dark-surface">
                    <h2 className="text-lg font-semibold mb-3">주문</h2>
                    <p className="text-sm text-text-secondary dark:text-dark-text-secondary">
                        최근 주문 내역과 배송 상태를 확인합니다.
                    </p>
                    <div className="mt-4">
                        <button>
                            <Link href="/orders">주문 내역</Link>
                        </button>
                    </div>
                </div>

                {/* 주소/결제 수단 등 확장 슬롯 */}
                <div className="rounded-2xl p-5 border border-border bg-surface dark:border-dark-border dark:bg-dark-surface">
                    <h2 className="text-lg font-semibold mb-3">설정</h2>
                    <ul className="list-disc pl-5 space-y-2 text-sm text-text-secondary dark:text-dark-text-secondary">
                        <li><Link className="underline" href="/mypage/addresses">배송지 관리</Link></li>
                        <li><Link className="underline" href="/mypage/security">보안 설정</Link></li>
                    </ul>
                </div>
            </section>
        </main>
    );
}
