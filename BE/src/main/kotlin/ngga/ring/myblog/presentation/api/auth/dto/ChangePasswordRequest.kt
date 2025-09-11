package ngga.ring.myblog.presentation.api.auth.dto

import jakarta.validation.constraints.NotBlank

data class ChangePasswordRequest(
    @field:NotBlank
    val oldPassword: String,
    @field:NotBlank
    val newPassword: String
)