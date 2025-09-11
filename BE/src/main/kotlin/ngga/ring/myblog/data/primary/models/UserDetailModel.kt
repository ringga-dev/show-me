package ngga.ring.myblog.data.primary.models


import ngga.ring.myblog.data.primary.db.entity.Gender
import java.time.LocalDateTime
import java.util.UUID

data class UserDetailModel(
    var userId: UUID? = null,
    var fullName: String? = null,
    var phone: String? = null,
    var dateOfBirth: LocalDateTime? = null,
    var gender: Gender? = null,
    var email: String? = null,
    var status: String? = null,
    var isVerified: Boolean? = false,
    var role: List<String>? = null,
    var address: String? = null,
    var city: String? = null,
    var province: String? = null,
    var postalCode: String? = null,
    var profilePictureUrl: String? = null,
    var bio: String? = null,
    val createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null
)