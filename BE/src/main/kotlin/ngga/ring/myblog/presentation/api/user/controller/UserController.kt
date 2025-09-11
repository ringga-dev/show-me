package ngga.ring.myblog.presentation.api.user.controller

import io.swagger.v3.oas.annotations.Operation
import ngga.ring.myblog.data.auth.service.AuthService
import ngga.ring.myblog.data.primary.db.entity.Gender
import ngga.ring.myblog.data.primary.service.UserService
import ngga.ring.myblog.presentation.api.Config
import ngga.ring.myblog.presentation.api.auth.dto.ForgotPasswordRequest
import ngga.ring.myblog.presentation.api.auth.dto.LoginRequest
import ngga.ring.myblog.presentation.api.auth.dto.RefreshTokenRequest
import ngga.ring.myblog.presentation.api.auth.dto.RegisterRequest
import ngga.ring.myblog.presentation.api.user.dto.UpdateUserRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import kotlin.String


@RestController
@RequestMapping("${Config.V1.URL}/user")
class UserController(
    private val userService: UserService,
) {
    @Operation(summary = "Me", description = "Get User Detail")
    @GetMapping("/me")
    fun getUserById(@RequestHeader("Authorization") token: String): ResponseEntity<Any> {
        return ResponseEntity.ok(userService.getUserById(token))
    }


    @Operation(summary = "Update", description = "Update User")
    @PutMapping("/me")
    fun updateUser(
        @RequestHeader("Authorization") token: String,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(
            userService.updateUser(
                token = token,
                fullName = request.fullName,
                dateOfBirth = request.dateOfBirth,
                phone = request.phone,
                gender = request.gender,
                address = request.address,
                city = request.city,
                province = request.province,
                postalCode = request.postalCode,
                profilePictureUrl = request.profilePictureUrl,
                bio = request.bio,
            )
        )
    }

    @Operation(summary = "Register", description = "Register User")
    @PostMapping("/add")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Any> {
        return ResponseEntity.ok(
            userService.addUser(
                userName = request.userName,
                email = request.email,
                password = request.password,
                role = request.role,
                status = request.status
            )
        )
    }

    @Operation(summary = "Delete", description = "Delete User")
    @DeleteMapping("/{userId}/delete")
    fun deleteUserById(
        @RequestHeader("Authorization") token: String,
        @PathVariable("userId") id: String
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(userService.deleteUserById(token, id))
    }

    @Operation(summary = "Get All User", description = "Get All User")
    @GetMapping("/all")
    fun getAllUser(): ResponseEntity<Any> {
        return ResponseEntity.ok(userService.getAllUser())
    }

    @Operation(summary = "Get User By UserName", description = "Get User By UserName")
    @GetMapping("/username")
    fun getUserByUsername(@RequestHeader("userName") userName: String): ResponseEntity<Any> {
        return ResponseEntity.ok(userService)
    }

    @Operation(summary = "Get User By Email", description = "Get User By Email")
    @GetMapping("/email")
    fun getUserByEmail(@RequestHeader("email") email: String): ResponseEntity<Any> {
        return ResponseEntity.ok(userService)
    }


}