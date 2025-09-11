package ngga.ring.myblog.presentation.api.auth.controller

import io.swagger.v3.oas.annotations.Operation
import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.data.auth.service.AuthService
import ngga.ring.myblog.presentation.api.Config
import ngga.ring.myblog.presentation.api.auth.dto.ForgotPasswordRequest
import ngga.ring.myblog.presentation.api.auth.dto.LoginRequest
import ngga.ring.myblog.presentation.api.auth.dto.RefreshTokenRequest
import ngga.ring.myblog.presentation.api.auth.dto.RegisterRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("${Config.V1.URL}/auth")
class AuthController(
    private val authService: AuthService,
) {
    @Operation(summary = "Login", description = "This fungtion login in user by email and password")
    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): ResponseEntity<Any> {
        val data = authService.postLoginUser(req.email, req.password)
        return ResponseEntity.ok(data)
    }

    @Operation(summary = "Register", description = "This action register a user")
    @PostMapping("/register")
    fun register(@RequestBody req: RegisterRequest): ResponseEntity<Any> {
        val data = authService.postRegisterUser(
            req.userName,
            req.email,
            req.password,
            req.confirmPassword,
            req.role,
            req.status,
            req.isVerified
        )
        return ResponseEntity.ok(data)
    }


    @PostMapping(value = ["/forgot-password"])
    fun forgotPassword(@RequestBody request: ForgotPasswordRequest): ResponseEntity<Any> {
        val data = authService.forgotPassword(request.email)
        return ResponseEntity.ok(data)
    }


    @PostMapping(value = ["/refresh-token"])
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<Any> {
        val data = authService.refreshToken(token = request.token)
        return ResponseEntity.ok(data)

    }

}