package ngga.ring.myblog.presentation.api.user.dto

import ngga.ring.myblog.data.primary.db.entity.Gender
import java.time.LocalDateTime

data class UpdateUserRequest(
    var fullName: String? = null,
    var dateOfBirth: LocalDateTime? = null,
    var phone: String? = null,
    var gender: Gender? = null,
    var address: String? = null,
    var city: String? = null,
    var province: String? = null,
    var postalCode: String? = null,
    var profilePictureUrl: String? = null,
    var bio: String? = null
)