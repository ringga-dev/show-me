package ngga.ring.myblog.data.auth.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import ngga.ring.myblog.core.converter.StringListConverter
import ngga.ring.myblog.data.auth.models.LoginModel
import ngga.ring.myblog.data.auth.models.RegisterModel
import ngga.ring.myblog.data.auth.models.TokenModel
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "m_user_auth")
data class AuthEntity(

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    var id: UUID = UUID.randomUUID(),

    @Column(name = "user_name", length = 255, unique = true, nullable = false)
    var userName: String = "",

    @Column(name = "email", length = 255, unique = true, nullable = false)
    var email: String = "",

    @Column(name = "password", length = 255, nullable = false)
    var password: String = "",

    @Convert(converter = StringListConverter::class)
    @Column(name = "role", length = 255, nullable = false)
    var role: List<String>? = null,

    @Column(name = "status", length = 255, nullable = true)
    var status: String? = null,

    @Column(name = "is_verified", length = 255, nullable = false)
    var isVerified: Boolean? = false,

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now()

){
    fun toRegisterModel(): RegisterModel {
        return RegisterModel(
            id = id.toString(),
            userName = userName,
            email = email,
            password = password,
            role = role,
            status = status,
            isVerified = isVerified,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun toLoginModel(
        token: TokenModel? = null
    ): LoginModel {
        return LoginModel(
            userName = userName,
            email = email,
            role = role,
            status = status,
            isVerified = isVerified,
            token = token
        )
    }
}