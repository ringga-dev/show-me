// ========== TOKEN ==========

export interface LoginTokenModel {
    accessToken?: string | null
    refreshToken?: string | null
    tokenType?: string | null
    loginAt?: number | null
}

export interface TokenModel {
    id?: string | null
    name?: string | null
    token?: string | null
    quota?: number | null
    usageCount?: number | null
    expiredAt?: Date | null
    isActive?: boolean | null
    note?: string | null
    createdAt?: Date | null
    updatedAt?: Date | null
}