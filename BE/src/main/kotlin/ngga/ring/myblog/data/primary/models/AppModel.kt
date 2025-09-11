package ngga.ring.myblog.data.primary.models

import java.time.LocalDateTime
import java.util.UUID

data class AppModel(
    val id: UUID? = null,
    val name: String? = null,       // nama aplikasi
    val slug: String? = null,         // URL-friendly key
    val image: String? = null,      // gambar aplikasi
    val description: String? = null,
    val isActive: Boolean? = null,       // aplikasi bisa punya beberapa role
    val features: List<FeatureModel>? = null,
    val ads: List<AdsModel>? = null,
    val createdAt: LocalDateTime? = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = LocalDateTime.now()
)

data class FeatureModel(
    val title: String? = null,
    val description: String? = null,
    val isActive: Boolean? = null
)

data class AdsModel(
    val title: String? = null,
    val image: String? = null,
    val description: String?= null,
    val isActive: Boolean? = null
)
