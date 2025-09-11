package ngga.ring.myblog.presentation.api.user.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class BlogRequest(
    @field:NotBlank(message = "title is required")
    @field:Size(max = 255, message = "title max 255 chars")
    val title: String,

    @field:Size(max = 255, message = "slug max 255 chars")
    val slug: String? = null,

    val excerpt: String? = null,
    val content: String? = null,
    val coverImage: String? = null,
    val published: Boolean? = null,
    val metaTitle: String? = null,
    val metaDescription: String? = null,
    val isActive: Boolean? = null,

    /** Simpan sebagai list; controller akan join ke CSV */
    val categories: List<String>? = null,
    val tags: List<String>? = null
)