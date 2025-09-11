package ngga.ring.myblog.presentation.api.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size


data class RegisterRequest(

    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 5, max = 100, message = "Username harus memiliki minimal 5 karakter dan maksimal 100 karakter")
    val userName: String,

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Email tidak valid")
    val email: String,

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, message = "Password harus memiliki minimal 6 karakter")
    val password: String,

    @NotBlank(message = "Confirm Password tidak boleh kosong")
    @Size(min = 6, message = "Confirm Password harus memiliki minimal 6 karakter")
    val confirmPassword: String,

    @NotBlank(message = "Role tidak boleh kosong")
    val role: List<String>,

    @NotBlank(message = "Status tidak boleh kosong")
    val status: String,

    val isVerified: Boolean


)