// /types/account.ts
import type { IsoDateTime, Email, Phone } from './common';
import type { AccountId } from './ids';

export interface MemberDto {
    id?: number;
    email?: string;
    password?: string;       // provider=LOCAL일 때만 의미
    nickname?: string;
    name?: string;
    phone?: Phone;
    address1?: string;
    address2?: string;
    provider?: string | null;       // 예: 'LOCAL' | 'GOOGLE' | 'KAKAO' | ...
    provider_id?: string | null;
    verified?: boolean;
    createdAt?: string; // DATETIME
    role: 'SELLER' | 'USER' | null;                   // 기본 'USER' (서버 기본값)
    grade?: string | null;          // 기본 'BASIC'
}

export type user = MemberDto;

export type AccountVM = {
    id: AccountId;
    email: Email;
    name: string;                   // null이면 '' 처리
    nickname?: string;
    displayName: string;            // nickname || name || email local-part
    phone?: Phone;
    address?: string;
    provider: string;               // 미정 값 허용
    providerId?: string;
    verified: boolean;
    role: string;                   // 'USER' | 'ADMIN' | ... (서버 소스오브트루스)
    grade: string;                  // 'BASIC' | ...
    createdAt?: IsoDateTime;
};

export type SignInRequest = {
    email: Email;
    password: string
};

export type UpdateAccountRequest = {
    name?: string | null;
    nickname?: string | null;
    phone?: Phone | null;
    address1?: string | null;
    address2?: string | null;
    // 비밀번호 변경은 별도 API가 일반적
};

export type SessionUser = Pick<AccountVM, 'id' | 'email' | 'displayName' | 'role'>;
