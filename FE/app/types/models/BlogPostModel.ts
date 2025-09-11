export interface BlogPost {
    slug: string
    title: string
    excerpt: string
    image: string
    category: string[]
    date: string
    readTime: number
    views: number
    likes: number
}

export interface Paginated<T> {
    data: T[]
    page: number
    perPage: number
    total: number
    totalPages: number
}