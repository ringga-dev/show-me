// ========== Ads ==========

export interface BaseResponse<T> {
    code?: number | null;
    status?: string | null;
    message?: string[] | null;
    data?: T | null;
}

