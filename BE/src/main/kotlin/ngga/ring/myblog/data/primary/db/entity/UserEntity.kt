package ngga.ring.myblog.data.primary.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(name = "m_users")
data class UserEntity(
    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    var userId: UUID? = UUID.randomUUID(),

    @Column(name = "full_name", nullable = false)
    var fullName: String? = null,

    @Column(name = "phone", nullable = true, length = 20)
    var phone: String? = null,

    @Column(name = "date_of_birth", nullable = true)
    var dateOfBirth: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = true)
    var gender: Gender? = null,

    @Column(name = "address", nullable = true, columnDefinition = "TEXT")
    var address: String? = null,

    @Column(name = "city", nullable = true, length = 100)
    var city: String? = null,

    @Column(name = "province", nullable = true, length = 100)
    var province: String? = null,

    @Column(name = "postal_code", nullable = true, length = 10)
    var postalCode: String? = null,

    @Column(name = "profile_picture_url", nullable = true)
    var profilePictureUrl: String? = null,

    @Column(name = "bio", columnDefinition = "TEXT", nullable = true)
    var bio: String? = null,


    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class Gender {
    MALE, FEMALE, OTHER, PREFER_NOT_TO_SAY
}