package ngga.ring.myblog.presentation.api.auth.dto

data class LoginRequest(
    val email: String,
    val password: String
)