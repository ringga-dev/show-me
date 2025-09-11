package ngga.ring.myblog.presentation.api.user.dto

import ngga.ring.myblog.data.primary.models.AdsModel
import ngga.ring.myblog.data.primary.models.FeatureModel

data class AppDto(
    val name: String? = null,
    val slug: String? = null,
    val image: String? = null,
    val description: String? = null,
    val isActive: Boolean? = null,
    val features: List<FeatureModel>? = null,
    val ads: List<AdsModel>? = null,
)