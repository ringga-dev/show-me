package ngga.ring.myblog.data.auth.db.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity @Table(name = "m_tokens")
data class TokenEntity(
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "user_id", nullable = false)
    var userId: UUID? = null,

    @Column(name = "access_token", nullable = false, columnDefinition = "TEXT")
    var accessToken: String? = null,

    @Column(name = "refresh_token", nullable = false, columnDefinition = "TEXT")
    var refreshToken: String? = null,

    @Column(name = "token_type", nullable = false)
    var tokenType: String? = null,

    @Column(name = "come_in_on", nullable = false)
    var loginAt: LocalDateTime? = null,


    @Column(name = "created_at", updatable = false, nullable = false)
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now() // ✅ Tambahkan default value
)