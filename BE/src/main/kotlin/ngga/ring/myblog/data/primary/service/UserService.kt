package ngga.ring.myblog.data.primary.service

import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.data.auth.db.entity.AuthEntity
import ngga.ring.myblog.data.auth.repositories.AuthRepository
import ngga.ring.myblog.data.auth.service.AuthService
import ngga.ring.myblog.data.auth.service.JwtService
import ngga.ring.myblog.data.primary.db.entity.Gender
import ngga.ring.myblog.data.primary.db.entity.UserEntity
import ngga.ring.myblog.data.primary.models.UserDetailModel
import ngga.ring.myblog.data.primary.repositories.RoomMemberRepository
import ngga.ring.myblog.data.primary.repositories.UserRepository
import org.apache.coyote.BadRequestException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID
import kotlin.String


interface UserService {
    fun getUserById(token: String): Any
    fun addUser(
        userName: String,
        email: String,
        password: String,
        role: List<String>,
        status: String
    ): Any

    fun updateUser(
        token: String,
        fullName: String? = null,
        dateOfBirth: LocalDateTime? = null,
        phone: String? = null,
        gender: Gender? = null,
        address: String? = null,
        city: String? = null,
        province: String? = null,
        postalCode: String? = null,
        profilePictureUrl: String? = null,
        bio: String? = null,
    ): Any

    fun deleteUserById(token: String, id: String): Any

    fun getUserByUsername(userName: String): Any
    fun getUserByEmail(email: String): Any
    fun getAllUser(): Any
}

@Service
class UserServiceImpl(
    private val authRepository: AuthRepository,
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val roomMemberRepository: RoomMemberRepository,
    private val jwtService: JwtService,
) : UserService {

    override fun getUserById(token: String): BaseResponse<UserDetailModel> {
        return try {
            // 1. Ekstrak & validasi userId
            val userId = jwtService.getUserIdFromAccessToken(token)
                .let {
                    runCatching { UUID.fromString(it) }
                        .getOrElse { throw AccessDeniedException("Invalid userId format") }
                } ?: throw AccessDeniedException("Malformed token")

            // 2. Ambil data auth & user
            val auth = authRepository.findById(userId)
                .orElseThrow { AccessDeniedException("User not found") }

            val user = userRepository.findByUserId(userId)
                ?: userRepository.save(
                    UserEntity(
                        userId = auth.id,
                        fullName = auth.userName
                    )
                )


            // 3. Bangun response
            val detail = UserDetailModel(
                email = auth.email,
                role = auth.role,
                status = auth.status,
                fullName = user.fullName,
                phone = user.phone,
                dateOfBirth = user.dateOfBirth,
                gender = user.gender,
                address = user.address,
                city = user.city,
                province = user.province,
                postalCode = user.postalCode,
                profilePictureUrl = user.profilePictureUrl,
                bio = user.bio
            )

            BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = listOf("User details retrieved successfully"),
                data = detail
            )
        } catch (e: AccessDeniedException) {
            throw e
        } catch (e: Exception) {
            throw RuntimeException("Failed to retrieve user details: ${e.message}", e)
        }
    }


    override fun addUser(
        userName: String,
        email: String,
        password: String,
        role: List<String>,
        status: String
    ): BaseResponse<Any> {
        try {
            val data = authService.postRegisterUser(
                userName = userName,
                email = email,
                password = password,
                confirmPassword = password,
                role = role,
                status = status,
                isVerified = true
            )

            userRepository.findByUserId(UUID.fromString(data.data!!.id))
                ?: userRepository.save(
                    UserEntity(
                        userId = UUID.fromString(data.data!!.id),
                        fullName = data.data!!.userName
                    )
                )


            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = listOf("User details retrieved successfully"),
                data = data.data!!.copy(
                    password = password
                )
            )
        } catch (e: AccessDeniedException) {
            throw e
        } catch (e: Exception) {
            throw BadRequestException("Failed to retrieve user details: ${e.message}", e)
        }
    }

    @Transactional
    override fun updateUser(
        token: String,
        fullName: String?,
        dateOfBirth: LocalDateTime?,
        phone: String?,
        gender: Gender?,
        address: String?,
        city: String?,
        province: String?,
        postalCode: String?,
        profilePictureUrl: String?,
        bio: String?
    ): Any {
        try {
            val userId = jwtService.getUserIdFromAccessToken(token)
                .let {
                    runCatching { UUID.fromString(it) }
                        .getOrElse { throw AccessDeniedException("Invalid userId format") }
                    ?: throw AccessDeniedException("Malformed token")
                }
            val user = userRepository.findByUserId(userId)
                ?: throw AccessDeniedException("User not found")

            val saved = userRepository.save(
                user.copy(
                    fullName = fullName,
                    dateOfBirth = dateOfBirth,
                    phone = phone,
                    gender = gender,
                    address = address,
                    city = city,
                    province = province,
                    postalCode = postalCode,
                    profilePictureUrl = profilePictureUrl,
                    bio = bio
                )
            )
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = listOf("User details retrieved successfully"),
                data = saved
            )
        } catch (e: Exception) {
            throw BadRequestException("Failed to retrieve user details: ${e.message}", e)
        }
    }

    @Transactional
    override fun deleteUserById(token: String, id: String): Any {
        // 1) Ambil user peminta (requester) dari token
        val requesterId: UUID = runCatching {
            UUID.fromString(jwtService.getUserIdFromAccessToken(token))
        }.getOrElse { throw AccessDeniedException("Invalid userId format") }

        val requester = authRepository.findById(requesterId)
            .orElseThrow { AccessDeniedException("User not found") }

        // 2) Parse targetId
        val targetId = runCatching { UUID.fromString(id) }
            .getOrElse { throw BadRequestException("Invalid target userId format") }

        // 3) Ambil target user
        val target = authRepository.findById(targetId)
            .orElseThrow { BadRequestException("Target user not found") }

        // 4) Cek role (case-insensitive)
        fun hasAdminRole(roles: Collection<String>?): Boolean =
            roles?.any { it.equals("admin", ignoreCase = true) } == true

        val requesterIsAdmin = hasAdminRole(requester.role)
        val targetIsAdmin = hasAdminRole(target.role)

        // (opsional) cegah hapus diri sendiri jika tidak diizinkan
        if (requesterId == targetId) {
            throw AccessDeniedException("You cannot delete your own account")
        }

        // Hanya admin boleh hapus, dan admin tidak boleh hapus admin lain
        if (!requesterIsAdmin) throw AccessDeniedException("Access denied")
        if (targetIsAdmin) throw AccessDeniedException("Cannot delete another admin")

        try {
            // 5) Hapus CHILD dulu → lalu PARENT
            //    Sesuaikan dengan relasi yang kamu punya (room members, posts, comments, dll)
            roomMemberRepository.deleteByUserId(targetId)
            // … jika ada tabel anak lain, hapus di sini juga.

            // 6) Hapus user di repo utama
            authRepository.deleteById(targetId)
            // Jika kamu memang menyimpan user di dua tabel/repo, bersihkan keduanya:
            userRepository.deleteById(targetId)

            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = listOf("User deleted successfully"),
                data = null
            )
        } catch (e: DataIntegrityViolationException) {
            // Biasanya muncul saat masih ada FK lain yang nempel
            throw BadRequestException(
                "Failed to delete user due to existing references. " +
                        "Remove related data first or enable ON DELETE CASCADE.",
                e
            )
        } catch (e: AccessDeniedException) {
            throw e
        } catch (e: Exception) {
            throw BadRequestException("Failed to delete user: ${e.message}", e)
        }
    }

    override fun getUserByUsername(userName: String): Any {
        try {
            val data = authRepository.findByUserName(userName)
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = listOf("User details retrieved successfully"),
                data = data
            )
        } catch (e: Exception) {
            throw RuntimeException("Failed to retrieve user details: ${e.message}", e)
        }
    }

    override fun getUserByEmail(email: String): Any {
        try {
            val data = authRepository.findByEmail(email)
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = listOf("User details retrieved successfully"),
                data = data
            )
        } catch (e: Exception) {
            throw RuntimeException("Failed to retrieve user details: ${e.message}", e)
        }
    }

    @Transactional(readOnly = true)
    override fun getAllUser(): BaseResponse<List<UserDetailModel>> {
        return try {
            val users = userRepository.findAll()

            // Kumpulkan semua userId (yang tidak null)
            val userIds: List<UUID> = users.mapNotNull { it.userId }

            // Ambil semua auth sekaligus -> kurangi N+1
            val authList: List<AuthEntity> = authRepository.findAllById(userIds)
            val authById: Map<UUID, AuthEntity> = authList.associateBy { it.id }

            val payload: List<UserDetailModel> = users.map { u ->
                val uid = u.userId
                val auth = uid?.let { authById[it] } // bisa null kalau tidak ada padanannya

                UserDetailModel(
                    userId = uid,
                    fullName = u.fullName,
                    phone = u.phone,
                    dateOfBirth = u.dateOfBirth,
                    gender = u.gender,
                    email = auth?.email,            // aman kalau null
                    status = auth?.status,
                    isVerified = auth?.isVerified,
                    role = auth?.role,
                    address = u.address,
                    city = u.city,
                    province = u.province,
                    postalCode = u.postalCode,
                    profilePictureUrl = u.profilePictureUrl,
                    bio = u.bio,
                    createdAt = u.createdAt,
                    updatedAt = u.updatedAt
                )
            }

            BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = listOf("User all"),
                data = payload
            )
        } catch (e: Exception) {
            // boleh pakai logger juga di sini
            throw BadRequestException("Failed to retrieve user details: ${e.message}", e)
        }
    }


}