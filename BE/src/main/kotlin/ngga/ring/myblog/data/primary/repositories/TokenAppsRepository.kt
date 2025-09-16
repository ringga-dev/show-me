package ngga.ring.myblog.data.primary.repositories

import ngga.ring.myblog.data.primary.db.entity.TokenAppsEntity
import ngga.ring.myblog.data.primary.db.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TokenAppsRepository : JpaRepository<TokenAppsEntity, UUID> {

   fun findByToken(token: String): java.util.Optional<TokenAppsEntity>
}