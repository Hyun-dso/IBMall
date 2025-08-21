// /types/api.ts
export type ApiCode = 'SUCCESS' | 'FAIL';

/* 레거시(일반) */
export interface ApiSuccess<T> { code: 'SUCCESS'; message: string; data: T; }
export interface ApiFail { code: 'FAIL'; message: string; data: null; }
export type ApiResponse<T> = ApiSuccess<T> | ApiFail;

/* 관리자 */
export interface ApiSuccessAdmin<T> {
    success: true;
    data: T;
    message: string | null;
}
export interface ApiErrorPayloadAdmin {
    status: number;
    code: string;
    message: string;
}
export interface ApiFailAdmin {
    success: false;
    error: ApiErrorPayloadAdmin;
}
export type ApiResponseAdmin<T> = ApiSuccessAdmin<T> | ApiFailAdmin;

/* 가드 */
export function isAdminSuccess<T>(x: ApiResponseAdmin<T>): x is ApiSuccessAdmin<T> {
    return (x as any)?.success === true;
}

/* (선택) 레거시용 가드 유지 시 */
export function isSuccess<T>(x: ApiResponse<T>): x is ApiSuccess<T> {
    return (x as any)?.code === 'SUCCESS';
}
