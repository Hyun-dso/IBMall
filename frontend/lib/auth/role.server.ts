// /lib/auth/role.server.ts
// 서버 전용 역할 가드 유틸리티
// 전제: /api/members/me 가 인증된 사용자 정보를 반환하며, data.role 은 User['role'] 문자열이다.

import { redirect } from 'next/navigation';
import { getUserFromServer } from '@/lib/api/members.server';
import type { User } from '@/types/auth';

/** 현재 사용자 조회(미인증 시 null). 서버 컴포넌트에서만 사용. */
export async function getCurrentUser(): Promise<User | null> {
    return (await getUserFromServer()) as any;
}

/** 인증 필수 가드: 미인증이면 redirectTo로 리다이렉트, 성공 시 User 반환. */
export async function requireAuthenticated(redirectTo = '/signin'): Promise<User> {
    const me = (await getUserFromServer()) as any;
    if (!me) redirect(redirectTo);
    return me;
}

/** 단일 역할 가드: 역할 불일치면 /403으로 리다이렉트, 성공 시 User 반환. */
export async function requireRole(role: User['role'], redirectTo = '/signin'): Promise<User> {
    const me = (await getUserFromServer()) as any;
    if (!me) redirect(redirectTo);
    if (me.role !== role) redirect('/403');
    return me;
}

/** 다중 역할 가드: 목록에 없으면 /403, 성공 시 User 반환. */
export async function requireRoles(
    roles: ReadonlyArray<User['role']>,
    redirectTo = '/signin'
): Promise<User> {
    const me = (await getUserFromServer()) as any;
    if (!me) redirect(redirectTo);
    if (!roles.includes(me.role)) redirect('/403');
    return me;
}

/** 헬퍼: 보유 역할 여부. */
export function hasRole(me: User | null | undefined, role: User['role']): boolean {
    return !!me && me.role === role;
}

/** 헬퍼: 다중 역할 중 하나라도 보유 여부. */
export function hasAnyRole(
    me: User | null | undefined,
    roles: ReadonlyArray<User['role']>
): boolean {
    return !!me && roles.includes(me.role);
}
