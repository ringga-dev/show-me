// ========== USER ==========
export interface UserModel {
    userName?: string | null;
    email?: string | null;
    role?: string[] | null;
    status?: string | null;
    fullName?: string | null;
    phone?: string | null;
    dateOfBirth?: Date | null;
    gender?: string | null;
    address?: string | null;
    city?: string | null;
    province?: string | null;
    postalCode?: string | null;
    profilePictureUrl?: string | null;
    bio?: string | null;
}

export interface UsersModel {
    userId?: string | null
    fullName?: string | null
    phone?: string | null
    dateOfBirth?: string | null
    gender?: string | null
    email?: string | null
    status?: string | null
    isVerified?: string | null
    role?: string[] | null
    address?: string | null
    city?: string | null
    province?: string | null
    postalCode?: string | null
    profilePictureUrl?: string | null
    bio?: string | null
    createdAt?: Date | null
    updatedAt?: Date | null
}
