// /types/auth.ts

export interface SignupRequest {
    email: string;
    password: string;
    confirmPassword: string;
    name: string;
    nickname: string;
    phone?: string;
    address?: string;
}

export interface SignupResponse {
    message: string; // "회원가입 성공! 이메일 인증 링크를 확인하세요."
}

export interface SigninRequest {
    email: string;
    password: string;
}

export interface SigninResponse {
    message: string; // "로그인 성공"
    token: string;   // JWT (현재 백엔드 기준)
}

export interface LogoutResponse {
    message: string; // "로그아웃 완료 (프론트에서 토큰 삭제 필요)"
}

export interface User {
    id: number;
    email: string;
    name: string;
    nickname: string;
    phone: string;
    address: string;
    provider: 'local' | 'google';
    verified: boolean;
    grade: 'BASIC' | 'ADMIN' | string;
    createdAt: string; // ISO 8601 형식
}

export interface ProfileResponse extends User { }

export interface ProfileUpdateRequest {
    name: string;
    nickname: string;
    phone: string;
    address: string;
}

export interface ProfileUpdateResponse {
    message: string; // "회원 정보 수정 완료"
}
