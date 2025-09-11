package ngga.ring.myblog.data.primary.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "t_token_apps")
data class TokenAppsEntity(
    @Id
    @Column(name = "token_id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = false)
    val name: String? = null,       // nama aplikasi

    @Column(name = "token", nullable = false, unique = true)
    val token: String? = null,         // URL-friendly key

    @Column(name = "quota", nullable = true)
    val quota: Int? = null,      // gambar aplikasi

    @Column(name = "usage_count", nullable = true,)
    val usageCount: Int? = null,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @Column(name = "expired_at", nullable = false)
    val expiredAt: LocalDateTime = LocalDateTime.now(),      // aplikasi bisa punya beberapa role

    @Column(name = "note", nullable = false, columnDefinition = "LONGTEXT")
    val note: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
