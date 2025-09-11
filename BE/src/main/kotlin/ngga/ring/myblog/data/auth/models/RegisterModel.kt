package ngga.ring.myblog.data.auth.models



import ngga.ring.myblog.data.auth.db.entity.AuthEntity
import java.time.LocalDateTime

data class RegisterModel(
    var id: String? = null,
    var userName: String? = null,
    var email: String? = null,
    var password: String? = null,
    var role: List<String>? = null,
    var status: String? = null,
    var isVerified: Boolean? = false,
    var createdAt: LocalDateTime? = null,
    var updatedAt: LocalDateTime? = null
){
   fun toAuthModel(): AuthEntity {
        return AuthEntity(
            id = id?.let { java.util.UUID.fromString(it) } ?: java.util.UUID.randomUUID(),
            userName = userName?:"",
            email = email?: "",
            password = password?:"",
            role = role,
            status = status,
            isVerified = isVerified,
            createdAt = createdAt?: LocalDateTime.now(),
            updatedAt = updatedAt?: LocalDateTime.now()
        )
    }
}