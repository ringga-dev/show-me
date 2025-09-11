package ngga.ring.myblog.data.auth.repositories

import ngga.ring.myblog.data.auth.db.entity.AuthEntity
import ngga.ring.myblog.data.primary.repositories.UserRepository
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AuthRepository : JpaRepository<AuthEntity, UUID> {
    fun findByUserName(username: String): AuthEntity?
    fun findByEmail(email: String): AuthEntity?
    fun findByEmailOrUserName(username: String, email: String): AuthEntity?
}