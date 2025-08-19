// /lib/validators/engine.ts
import type { AnyForm, ErrorMap, FormType, SignupForm, ProfileUpdateForm, GuestPaymentForm } from '@/types/forms';
import { isEmail, isStrongPassword, isNickname, isMobile010, notBlank } from './rules';

type Rule<T> = (value: any, form: T) => string | undefined;
type FieldSchema<T> = { required?: boolean | ((form: T) => boolean); rules?: Rule<T>[] };
export type Schema<T> = Partial<Record<keyof T, FieldSchema<T>>>;

function checkRequired<T>(cfg: FieldSchema<T> | undefined, form: T, value: any): string | undefined {
    if (!cfg?.required) return undefined;
    const must = typeof cfg.required === 'function' ? cfg.required(form) : cfg.required;
    if (!must) return undefined;
    if (value === undefined || value === null || String(value).trim() === '') return '모든 항목을 입력해야 해요.';
    return undefined;
}

export function validateBySchema<T extends Record<string, any>>(form: T, schema: Schema<T>) {
    const errors: ErrorMap<T> = {};
    for (const k in schema) {
        const cfg = schema[k as keyof T]!;
        const val = form[k as keyof T];

        const reqMsg = checkRequired(cfg, form, val);
        if (reqMsg) {
            errors[k as keyof T] = reqMsg;
            continue;
        }
        if (val === undefined || val === null || String(val) === '') continue;

        for (const rule of cfg.rules ?? []) {
            const msg = rule(val, form);
            if (msg) { errors[k as keyof T] = msg; break; }
        }
    }
    return { ok: Object.keys(errors).length === 0, errors };
}

/* 스키마 레지스트리: 단일 진입점로 사용 */
const signupSchema: Schema<SignupForm> = {
    email: { required: true, rules: [(v) => (isEmail(v) ? undefined : '올바른 이메일 형식이 아니에요')] },
    password: { required: true, rules: [(v) => (isStrongPassword(v) ? undefined : '비밀번호는 8자 이상, 숫자와 문자를 포함해야 해요')] },
    confirmPassword: { required: true, rules: [(v, f) => (v === f.password ? undefined : '비밀번호가 일치하지 않아요')] },
    name: { required: true, rules: [(v) => (notBlank(v) ? undefined : '이름을 입력해야 해요')] },
    nickname: { required: true, rules: [(v) => (isNickname(v) ? undefined : '닉네임은 2~20자, 특수문자 불가에요')] },
    phone: { required: true, rules: [(v) => (isMobile010(v) ? undefined : '올바른 연락처 형식이 아니에요')] },
    address1: { required: true, rules: [(v) => (notBlank(v) ? undefined : '주소를 입력해야 해요')] },
    address2: { required: true, rules: [(v) => (notBlank(v) ? undefined : '상세주소를 입력해야 해요')] },
};

const profileUpdateSchema: Schema<ProfileUpdateForm> = {
    email: { rules: [(v) => (isEmail(v) ? undefined : '올바른 이메일 형식이 아니에요')] },
    name: { rules: [(v) => (notBlank(v) ? undefined : '이름은 공백일 수 없어요')] },
    nickname: { rules: [(v) => (isNickname(v) ? undefined : '닉네임은 2~20자, 특수문자 불가에요')] },
    phone: { rules: [(v) => (isMobile010(v) ? undefined : '올바른 연락처 형식이 아니에요')] },
    address1: { rules: [(v) => (notBlank(v) ? undefined : '주소는 공백일 수 없어요')] },
    address2: { rules: [(v) => (notBlank(v) ? undefined : '상세주소는 공백일 수 없어요')] },
    newPassword: {
        rules: [(v) => (v ? (isStrongPassword(v) ? undefined : '비밀번호는 8자 이상, 숫자와 문자를 포함해야 해요') : undefined)],
    },
    confirmNewPassword: {
        rules: [(v, f) => (f.newPassword ? (v === f.newPassword ? undefined : '새 비밀번호가 일치하지 않아요') : undefined)],
    },
};

const guestPaymentSchema: Schema<GuestPaymentForm> = {
    buyerName: { required: true, rules: [(v) => (notBlank(v) ? undefined : '구매자 이름을 입력해야 해요')] },
    buyerEmail: { required: true, rules: [(v) => (isEmail(v) ? undefined : '올바른 이메일 형식이 아니에요(구매자)')] },
    buyerPhone: { required: true, rules: [(v) => (isMobile010(v) ? undefined : '올바른 연락처 형식이 아니에요(구매자)')] },
    address1: { required: true, rules: [(v) => (notBlank(v) ? undefined : '주소를 입력해야 해요')] },
    address2: { required: true, rules: [(v) => (notBlank(v) ? undefined : '상세주소를 입력해야 해요')] },
    recipientName: { required: (f) => f.sendToOther, rules: [(v) => (notBlank(v) ? undefined : '수령인 이름을 입력해야 해요')] },
    recipientPhone: { required: (f) => f.sendToOther, rules: [(v) => (isMobile010(v) ? undefined : '연락처 형식이 아니에요(수령인)')] },
};

/* 단일 진입점 */
const SCHEMAS: Record<FormType, Schema<any>> = {
    signup: signupSchema,
    profileUpdate: profileUpdateSchema,
    guestPayment: guestPaymentSchema,
};

export function validate(formType: FormType, form: AnyForm) {
    return validateBySchema<any>(form, SCHEMAS[formType]);
}

export function validateField<T extends AnyForm, K extends keyof T>(
    formType: FormType,
    key: K,
    form: T
): string | undefined {
    const { errors } = validate(formType, form);
    return errors[key as keyof T] as string | undefined;
}
