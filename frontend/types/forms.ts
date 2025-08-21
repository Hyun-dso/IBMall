// /types/forms.ts
export type FormType = 'signup' | 'profileUpdate' | 'guestPayment';

export interface SignupForm {
    email: string;
    password: string;
    confirmPassword: string;
    name: string;
    nickname: string;
    phone: string;
    address1: string;
    address2: string;
}

export interface ProfileUpdateForm {
    email?: string;
    password?: string;          // 현재 비밀번호(서버 검증 용)
    newPassword?: string;
    confirmNewPassword?: string;
    name?: string;
    nickname?: string;
    phone?: string;
    address1?: string;
    address2?: string;
}

export interface GuestPaymentForm {
    buyerName: string;
    buyerEmail: string;
    buyerPhone: string;
    address1: string;
    address2: string;
    sendToOther: boolean;
    recipientName?: string;
    recipientPhone?: string;
}

export type AnyForm = SignupForm | ProfileUpdateForm | GuestPaymentForm;
export type ErrorMap<T> = Partial<Record<keyof T, string>>;
