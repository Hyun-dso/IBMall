// /types/api.ts
export type ApiCode = 'SUCCESS' | 'FAIL';

export interface ApiSuccess<T> {
    code: 'SUCCESS';
    message: string;
    data: T;
}

export interface ApiFail {
    code: 'FAIL';
    message: string;
    data: null;
}

export type ApiResponse<T> = ApiSuccess<T> | ApiFail;

export function isSuccess<T>(x: ApiResponse<T>): x is ApiSuccess<T> {
    return x.code === 'SUCCESS';
}
