package ngga.ring.myblog.presentation.api.auth.dto

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @field:NotBlank
    val token: String
)