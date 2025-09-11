package ngga.ring.myblog.data.primary.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(name = "t_promotion")
data class PromotionEntity(
    @Id
    @Column(name = "promotion_id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = false)
    val name: String? = null,       // nama aplikasi

    @Column(name = "description", nullable = true, columnDefinition = "LONGTEXT")
    val description: String? = null,

    @Column(name = "rating", nullable = false, columnDefinition = "TEXT")
    val rating: Float? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "TEXT")
    val type: TypeStatus? = null,

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    val url: String? = null,

    @Column(name = "pos", nullable = false)
    val pos: Int? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class TypeStatus { IMAGE, VIDEO }


