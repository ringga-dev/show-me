package ngga.ring.myblog.presentation.api.auth.dto

import jakarta.validation.constraints.NotBlank

data class ResetPasswordRequest(
    @field:NotBlank
    val token: String,
    @field:NotBlank
    val password: String
)