// ========== USER ==========
import type {LoginTokenModel} from "~/types/models/TokenModel";

export interface LoginModel {
    userName?: string | null;
    email?: string | null;
    role?: string[] | null;
    status?: string | null;
    isVerified?: boolean | null;
    token?: LoginTokenModel | null

}