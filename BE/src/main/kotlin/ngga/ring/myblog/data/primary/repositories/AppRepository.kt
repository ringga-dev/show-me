package ngga.ring.myblog.data.primary.repositories

import ngga.ring.myblog.data.primary.db.entity.AppEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional
import java.util.UUID

interface AppRepository : JpaRepository<AppEntity, UUID> {
    fun findBySlug(slug: String): Optional<AppEntity>
    fun existsBySlug(slug: String): Boolean
}