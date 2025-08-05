// /types/password.ts

export interface PasswordResetLinkRequest {
    email: string;
}

export interface PasswordResetLinkResponse {
    message: string; // "비밀번호 재설정 링크가 이메일로 전송되었습니다."
}

export interface PasswordResetRequest {
    token: string;
    newPassword: string;
}

export interface PasswordResetResponse {
    message: string; // "비밀번호가 성공적으로 변경되었습니다"
}
