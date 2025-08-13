// /types/forms.ts
export interface SignupForm {
    email: string;
    password: string;
    confirmPassword: string;
    name: string;
    nickname: string;
    phone: string;     // 선택 입력이지만 폼 내부에서는 빈 문자열
    address1: string;  // 선택 입력(주소)
    address2: string;  // 선택 입력(상세주소)
}
