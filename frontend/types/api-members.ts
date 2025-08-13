// /types/api-members.ts
import type { ApiResponse } from './api';

export interface SignupPayload {
    email: string;
    password: string;
    confirmPassword: string;
    name: string;
    nickname: string;
    phone?: string | null;
    address?: string | null;
}
export type SignupResponse = ApiResponse<null>;
