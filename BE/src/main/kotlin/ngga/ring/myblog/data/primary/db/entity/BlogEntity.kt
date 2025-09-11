package ngga.ring.myblog.data.primary.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import ngga.ring.myblog.core.converter.StringToAdsListConverter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "t_blog",
    indexes = [
        Index(name = "idx_blog_slug", columnList = "slug", unique = true),
        Index(name = "idx_blog_active", columnList = "is_active"),
        Index(name = "idx_blog_published", columnList = "published")
    ]
)
data class BlogEntity(
    @Id
    @Column(name = "blog_id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "title", nullable = false, length = 255)
    var title: String,

    @Column(name = "slug", nullable = false, unique = true, length = 255)
    var slug: String,

    @Column(name = "excerpt", columnDefinition = "TEXT")
    var excerpt: String? = null,

    @Column(name = "content", columnDefinition = "LONGTEXT")
    var content: String? = null,

    @Column(name = "cover_image", columnDefinition = "LONGTEXT")
    var coverImage: String? = null,

    @Column(name = "published", nullable = false)
    var published: Boolean = false,

    @Column(name = "meta_title", length = 255)
    var metaTitle: String? = null,

    @Column(name = "meta_description", columnDefinition = "TEXT")
    var metaDescription: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Convert(converter = StringToAdsListConverter::class)
    @Column(name = "categories", columnDefinition = "LONGTEXT")
    var categories: List<String>? = null,

    @Convert(converter = StringToAdsListConverter::class)
    @Column(name = "tags", columnDefinition = "LONGTEXT")
    var tags: List<String>? = null,

    @Column(name = "view_count", nullable = false)
    var viewCount: Long = 0,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    /* Soft-delete flag */
    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
)
