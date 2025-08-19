// /types/email.ts

export interface EmailCodeSendRequest {
    email: string;
}

export interface EmailCodeSendResponse {
    message: string; // "이메일 인증코드 전송 완료"
}

export interface EmailCodeVerifyRequest {
    email: string;
    code: string;
}

export interface EmailCodeVerifyResponse {
    message: string; // "이메일 인증 성공"
}
