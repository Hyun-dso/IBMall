// /lib/validators/engine.ts
import type { SignupForm } from '@/types/forms';
import {
    MAX_NAME,
    MAX_NICKNAME,
    isValidEmailFormat,
    isValidPhoneLength,
    joinAddress,
    isValidAddressLength,
} from './rules';

type SchemaName = 'signup';
type FieldName =
    | 'email'
    | 'password'
    | 'confirmPassword'
    | 'name'
    | 'nickname'
    | 'phone'
    | 'address1'
    | 'address2';

export interface ValidateResult {
    ok: boolean;
    errors: Partial<Record<FieldName, string>>;
}

/**
 * 필드 단건 검증
 */
export function validateField(schema: SchemaName, field: FieldName, form: SignupForm): string | undefined {
    if (schema !== 'signup') return;

    switch (field) {
        case 'email': {
            if (!form.email.trim()) return '이메일을 입력해야 해요';
            if (!isValidEmailFormat(form.email.trim())) return '이메일 형식이 올바르지 않아요';
            return;
        }
        case 'password': {
            if (!form.password) return '비밀번호를 입력해야 해요';
            return;
        }
        case 'confirmPassword': {
            if (!form.confirmPassword) return '비밀번호 확인을 입력해야 해요';
            if (form.password !== form.confirmPassword) return '비밀번호가 일치하지 않아요';
            return;
        }
        case 'name': {
            const v = form.name.trim();
            if (!v) return '이름을 입력해야 해요';
            if (v.length > MAX_NAME) return `이름은 ${MAX_NAME}자 이하여야 해요`;
            return;
        }
        case 'nickname': {
            const v = form.nickname.trim();
            if (!v) return '닉네임을 입력해야 해요';
            if (v.length > MAX_NICKNAME) return `닉네임은 ${MAX_NICKNAME}자 이하여야 해요`;
            return;
        }
        case 'phone': {
            // 선택 입력: 값이 있을 때만 길이 검증
            if (form.phone && !isValidPhoneLength(form.phone)) return '연락처 길이가 허용 범위를 초과했어요';
            return;
        }
        case 'address1':
        case 'address2': {
            const joined = joinAddress(form.address1, form.address2);
            if (joined && !isValidAddressLength(joined)) return '주소 길이가 허용 범위를 초과했어요';
            return;
        }
        default:
            return;
    }
}

/**
 * 폼 전체 검증
 */
export function validate(schema: SchemaName, form: SignupForm): ValidateResult {
    if (schema !== 'signup') return { ok: true, errors: {} };

    const fields: FieldName[] = [
        'email',
        'password',
        'confirmPassword',
        'name',
        'nickname',
        'phone',
        'address1',
        'address2',
    ];

    const errors: Partial<Record<FieldName, string>> = {};

    for (const f of fields) {
        const msg = validateField(schema, f, form);
        if (msg) errors[f] = msg;
    }

    return { ok: Object.keys(errors).length === 0, errors };
}
