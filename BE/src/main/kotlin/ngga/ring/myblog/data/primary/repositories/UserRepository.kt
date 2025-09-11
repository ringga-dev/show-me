package ngga.ring.myblog.data.primary.repositories

import ngga.ring.myblog.data.primary.db.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {
    fun findByUserId(userId: UUID): UserEntity?
}