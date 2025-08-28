// /components/account/UnifiedAccountForm.tsx
'use client';
import { CENTER_CONTENT, FORM_CLASS } from '@/app/constants/styles';
import Logo from '@/components/ui/Logo';
import { signOut } from '@/lib/api/account.server';
import { showToast } from '@/lib/toast';

const group = 'auth.signout';
const redirect = '/';

function test() {
    try {
        signOut(redirect);
        showToast.success('로그아웃에 성공했어요', { group: group, persist: true });
    } catch {
        showToast.success('로그아웃에 실패했어요', { group: group });
    }
}

export default function SignoutPage() {
    return (
        <form className={`${FORM_CLASS} ${CENTER_CONTENT} flex flex-col justify-center items-center`}>
            <Logo size="lg" />
            <h2 className="text-lg font-semibold">계정 내려두기</h2>

            <button type="button" onClick={test} className="w-full mt-4 p-2 rounded-md border border-[var(--primary)] hover:cursor-pointer disabled:cursor-not-allowed">
                로그아웃
            </button>
        </form >
    );
}
