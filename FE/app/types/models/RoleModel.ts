// ========== ROLE ==========
import type {Permission} from "~/types/models/Permission";


export interface Role {
    id: string
    name: string // e.g. "Admin", "Editor", "Viewer"
    description?: string
    permissions: Permission[] // many-to-many
}

export function RoleModel(partial: Partial<Role> = {}): Role {
    return {
        id: partial.id ?? crypto.randomUUID(),
        name: partial.name ?? 'Untitled Role',
        description: partial.description ?? '',
        permissions: partial.permissions ?? [],
    }
}
