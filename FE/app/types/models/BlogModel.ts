export interface BlogModel {
    id?: string | null
    title?: string | null
    slug?: string | null
    excerpt?: string | null
    content?: string | null
    coverImage?: string | null
    published?: boolean | null
    metaTitle?: string | null
    metaDescription?: string | null
    isActive?: boolean | null
    categories?: string[] | null
    tags?: string[] | null
    viewCount?: number
    deletedAt?: Date | null
    createdAt?: Date | null
    updatedAt?: Date | null
}
