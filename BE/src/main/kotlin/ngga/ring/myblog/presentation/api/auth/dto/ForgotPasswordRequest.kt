package ngga.ring.myblog.presentation.api.auth.dto

import jakarta.validation.constraints.NotBlank

data class ForgotPasswordRequest(
    @field:NotBlank
    val email: String
)