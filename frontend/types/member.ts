// /types/member.ts
export type AuthProvider = 'local' | (string & {});
export type MemberGrade = 'BASIC' | (string & {});

export interface Member {
    id: number;
    email: string;
    name: string;
    nickname: string;
    phone: string | null;
    address: string | null;
    provider: AuthProvider;
    verified: boolean;
    grade: MemberGrade;
    createdAt: string; // ISO8601
}
