package ngga.ring.myblog.presentation.api.user.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class TokenDto (
    @field:NotBlank @field:Size(max = 150)
    val name: String,

    // token boleh dikirim oleh client; kalau mau selalu generate di server, bikin opsional
    @field:NotBlank @field:Size(min = 8, max = 200)
    val token: String,

    @field:PositiveOrZero
    val quota: Int = 0,

    @field:PositiveOrZero
    val usageCount: Int = 0,

    val isActive: Boolean = true,

    val expiredAt: LocalDateTime = LocalDateTime.now(),

    @field:Size(max = 10_000)
    val note: String? = null
)