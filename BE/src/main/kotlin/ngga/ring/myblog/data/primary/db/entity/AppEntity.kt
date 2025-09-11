package ngga.ring.myblog.data.primary.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import ngga.ring.myblog.core.converter.StringToAdsListConverter
import ngga.ring.myblog.core.converter.StringToFeatureListConverter
import ngga.ring.myblog.data.primary.models.AdsModel
import ngga.ring.myblog.data.primary.models.FeatureModel
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "t_apps")
 data class AppEntity(
    @Id
    @Column(name = "app_id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = false)
    val name: String? = null,       // nama aplikasi

    @Column(name = "slug", nullable = false)
    val slug: String? = null,         // URL-friendly key

    @Column(name = "image", nullable = true)
    val image: String? = null,      // gambar aplikasi

    @Column(name = "description", nullable = true, columnDefinition = "LONGTEXT")
    val description: String? = null,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean? = null,       // aplikasi bisa punya beberapa role

    @Column(name = "features", nullable = true, columnDefinition = "LONGTEXT")
    @Convert(converter = StringToFeatureListConverter::class)
    val features: List<FeatureModel>? = null,

    @Column(name = "ads", nullable = true, columnDefinition = "LONGTEXT")
    @Convert(converter = StringToAdsListConverter::class)
    val ads: List<AdsModel>? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
