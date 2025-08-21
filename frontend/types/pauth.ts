// /types/oauth.ts

export interface OAuthGoogleUrlResponse {
    url: string; // 구글 OAuth 인증 요청 URL
}

export interface OAuthGoogleCallbackRequest {
    code: string; // 구글이 redirect 시 넘겨주는 code
}

export interface TempUser {
    email: string;
    name: string;
    providerId: string;
}

export interface OAuthGoogleCallbackResponse {
    signupRequired: boolean;
    tempUser?: TempUser;
}
