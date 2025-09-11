package ngga.ring.myblog.data.primary.repositories

import ngga.ring.myblog.data.primary.db.entity.BlogEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface BlogRepository: JpaRepository<BlogEntity, UUID> {

   fun findBySlug(slug: String): BlogEntity?

    fun findBySlugAndDeletedAtIsNull(slug: String): BlogEntity?
    fun existsBySlug(slug: String): Boolean

    @Query(
        """
        SELECT b FROM BlogEntity b
        WHERE b.deletedAt IS NULL
          AND (:q IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%')) 
                         OR LOWER(b.slug) LIKE LOWER(CONCAT('%', :q, '%'))
                         OR LOWER(COALESCE(b.tags, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                         OR LOWER(COALESCE(b.categories, '')) LIKE LOWER(CONCAT('%', :q, '%')))
          AND (:published IS NULL OR b.published = :published)
          AND (:active IS NULL OR b.isActive = :active)
        """
    )
    fun search(
        q: String?,
        published: Boolean?,
        active: Boolean?,
        pageable: Pageable
    ): Page<BlogEntity>

    fun findByIdAndDeletedAtIsNull(id: UUID): BlogEntity?
}