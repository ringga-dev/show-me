// ========== APP ==========

export interface AppModel {
    id?: string | null;
    name?: string | null;          // nama aplikasi
    slug?: string | null;          // URL-friendly key
    image?: string | null;        // gambar aplikasi
    description?: string | null;
    isActive: boolean;       // aplikasi bisa punya beberapa role
    features?: FeatureModel[] | null;
    ads?: AdsModel[] | null
    createdAt?: Date | null;
    updatedAt?: Date | null;
}

export interface FeatureModel {
    title?: string | null;          // nama aplikasi
    description?: string | null;          // URL-friendly key
    isActive: boolean;       // aplikasi bisa punya beberapa role
}

export interface AdsModel {
    name?: string | null;          // nama aplikasi
    image?: string | null;        // gambar aplikasi
    description?: string;
    isActive: boolean;
}
