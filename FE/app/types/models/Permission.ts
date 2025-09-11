// ========== PERMISSION ==========
export interface Permission {
    id: string
    name: string // e.g. "project:create", "user:read"
    resource: string // nama resource (misalnya "project", "user")
    action: string // misalnya "create", "read", "update", "delete"
}
