package ngga.ring.myblog.presentation.api.user.dto

import ngga.ring.myblog.data.primary.db.entity.TypeStatus

data class PromotionDto(
   val name: String? = null,       // nama aplikasi
   val description: String? = null,
   val rating: Float? = null,
   val type: TypeStatus? = null,
   val url: String? = null,
   val pos: Int? = null,
)