package ngga.ring.myblog.data.auth.repositories

import ngga.ring.myblog.data.auth.db.entity.AuthEntity
import ngga.ring.myblog.data.auth.db.entity.TokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TokenRepository :JpaRepository<TokenEntity, UUID> {
    fun save( token: TokenEntity): TokenEntity?
    fun findByUserId(id: UUID): TokenEntity?
    fun findByUserIdAndRefreshToken(id: UUID, refreshToken: String): TokenEntity?
}