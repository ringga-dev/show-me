package ngga.ring.myblog.data.auth.service

import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.data.auth.db.entity.AuthEntity
import ngga.ring.myblog.data.auth.db.entity.TokenEntity
import ngga.ring.myblog.data.auth.models.LoginModel
import ngga.ring.myblog.data.auth.models.RegisterModel
import ngga.ring.myblog.data.auth.models.TokenModel
import ngga.ring.myblog.data.auth.repositories.AuthRepository
import ngga.ring.myblog.data.auth.repositories.TokenRepository
import org.apache.coyote.BadRequestException
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

interface AuthService {
    fun postLoginUser(
        email: String,
        password: String,
    ): BaseResponse<LoginModel>

    fun postRegisterUser(
        userName: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: List<String>,
        status: String,
        isVerified: Boolean,
    ): BaseResponse<RegisterModel>

    fun forgotPassword(email: String): BaseResponse<Any>
    fun refreshToken(token: String): BaseResponse<LoginModel>

}

@Service
class AuthServiceImpl(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository,
    private val jwtService: JwtService,
) : AuthService {
    private val passwordEncoder = BCryptPasswordEncoder()
    override fun postLoginUser(
        email: String,
        password: String
    ): BaseResponse<LoginModel> {
        val user = authRepository.findByEmail(email) ?: throw Exception("User not found")

        // Cek password
        val isPasswordMatch = passwordEncoder.matches(password, user.password)
        if (!isPasswordMatch) {
            throw BadRequestException("Password not match")
        }

        // Jika belum verifikasi email
//        if (user.isVerified != true) {
//            val otp = otpService.generateOtp(user.email)
//            emailService.sendOtpToEmail(user.email, otp)
//
//            return BaseResponse(
//                code = 200,
//                status = "SUCCESS",
//                message = arrayListOf("Please verify your email"),
//                data = LoginModel(
//                    userId = user.id.toString(),
//                    userName = user.userName,
//                    email = user.email,
//                    role = user.role,
//                    status = user.status,
//                    isVerified = user.isVerified,
//                    token = null
//                )
//            )
//        }

        // Generate token JWT
        val accessToken = jwtService.generateAccessToken(user.id.toString())
        val refreshToken = jwtService.generateRefreshToken(user.id.toString())

        // Simpan token (update kalau sudah ada)
        val tokenData = tokenRepository.findByUserId(user.id)
        val tokenSave = if (tokenData != null) {
            tokenRepository.save(
                tokenData.copy(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    tokenType = "Bearer",
                    loginAt = LocalDateTime.now(),
                )
            )
        } else {
            tokenRepository.save(
                TokenEntity(
                    userId = user.id,
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    tokenType = "Bearer",
                    loginAt = LocalDateTime.now(),
                )
            )
        }

        return BaseResponse(
            code = HttpStatus.OK.value(),
            status = HttpStatus.OK,
            message = arrayListOf("Login successful"),
            data = user.toLoginModel().copy(
                token = TokenModel(
                    accessToken = tokenSave?.accessToken,
                    refreshToken = tokenSave?.refreshToken,
                    tokenType = "Bearer",
                    loginAt = tokenSave?.loginAt,
                )
            )
        )
    }


    override fun postRegisterUser(
        userName: String,
        email: String,
        password: String,
        confirmPassword: String,
        role: List<String>,
        status: String,
        isVerified: Boolean
    ): BaseResponse<RegisterModel> {
        val userChekEmail = authRepository.findByEmail(email = email)
        val userChekName = authRepository.findByUserName(username = userName)

        if (userChekEmail != null) {
            throw BadRequestException("Email sudah terdaftar")
        }
        if (userChekName != null) {
            throw BadRequestException("Username sudah terdaftar")
        }
        if (password != confirmPassword) {
            throw BadRequestException("Password tidak sama")
        }

        // 1. Hash password menggunakan BCryptPasswordEncoder
        val hashPassword = passwordEncoder.encode(password)

        val user = AuthEntity(
            userName = userName,
            email = email,
            password = hashPassword,
            role = role,
            status = status,
            isVerified = isVerified,
        )
        val userData = authRepository.save(user)

        return BaseResponse(
            code = HttpStatus.OK.value(),
            status = HttpStatus.OK,
            message = arrayListOf("Success register user"),
            data = userData.toRegisterModel()
        )

    }

    override fun forgotPassword(email: String): BaseResponse<Any> {
        val user = authRepository.findByEmail(email)

        if (user != null) {
//            val token = createToken(body.email)
//            emailService.sendPasswordResetEmail(body.email, token, user.username ?: "")
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Login successful"),
            )
        } else {
            throw Exception("User tidak ditemukan")
        }

    }

    override fun refreshToken(token: String): BaseResponse<LoginModel> {
        val cekToken = jwtService.validateRefreshToken(token)
        if (cekToken) {
            val claims = jwtService.getUserIdFromRefreshToken(token)
            val userId = claims // ID user dari token
            val user = authRepository.findById(UUID.fromString(userId)).orElseThrow {
                throw Exception("User tidak ditemukan")
            }


            // Generate token JWT
            val accessToken = jwtService.generateAccessToken(user.id.toString())
            val refreshToken = jwtService.generateRefreshToken(user.id.toString())

            // Simpan token (update kalau sudah ada)
            val tokenData = tokenRepository.findByUserIdAndRefreshToken(user.id, token)

            if (tokenData != null) {
                val tokenSave = tokenRepository.save(
                    tokenData.copy(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        tokenType = "Bearer",
                        loginAt = LocalDateTime.now(),
                    )
                )
                return BaseResponse(
                    code = HttpStatus.OK.value(),
                    status = HttpStatus.OK,
                    message = arrayListOf("Login successful"),
                    data = user.toLoginModel().copy(
                        token = TokenModel(
                            accessToken = tokenSave?.accessToken,
                            refreshToken = tokenSave?.refreshToken,
                            tokenType = "Bearer",
                            loginAt = tokenSave?.loginAt,
                        )
                    )
                )
            } else {
                throw  Exception("Token tidak ditemukan")
            }

        } else {
            throw Exception("Token tidak valid")
        }
    }
}