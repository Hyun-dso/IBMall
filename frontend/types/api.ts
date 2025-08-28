// /types/api.ts
export type ApiSuccess<T> = { code: 'SUCCESS'; message: string; data: T };
export type ApiFail = { code: string; message: string; data?: unknown };
export type ApiResponse<T> = ApiSuccess<T> | ApiFail;
export function isSuccess<T>(r: ApiResponse<T>): r is ApiSuccess<T> { return (r as any).code === 'SUCCESS'; }
