package ngga.ring.myblog.data.primary.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import ngga.ring.myblog.core.converter.SocialLinkListJsonConverter
import ngga.ring.myblog.core.converter.StringListConverter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(name = "m_user_portfolio")
data class UserPortfolioEntity(
    @Id
    @Column(name = "user_portfolio_id", updatable = false, nullable = false)
    var id: UUID? = UUID.randomUUID(),

    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Column(name = "image", nullable = true)
    var image: String? = null,



    @Column(name = "email", nullable = false)
    var email: String? = null,

    @Column(name = "phone", nullable = false)
    var phone: String? = null,

    @Column(name = "address", nullable = false)
    var address: String? = null,

    @Convert(converter = StringListConverter::class)
    @Column(name = "titles", nullable = false, columnDefinition = "LONGTEXT")
    var titles: List<String>? = null,

    @Column(name = "description", nullable = false,  columnDefinition = "LONGTEXT")
    var description: String? = null,

    @Convert(converter = StringListConverter::class)
    @Column(name = "hash_tag", nullable = false,  columnDefinition = "LONGTEXT")
    var hashTag: List<String>? = null,

    @Convert(converter = SocialLinkListJsonConverter::class)
    @Column(name = "social_media", nullable = false,  columnDefinition = "LONGTEXT")
    var socialMedia: List<SocialLink>? = null,

    @Convert(converter = StringListConverter::class)
    @Column(name = "worked_with", nullable = false,  columnDefinition = "LONGTEXT")
    var workedWith: List<String>? = null,

    @Column(name = "about_me", nullable = false,  columnDefinition = "LONGTEXT")
    var aboutMe: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

data class SocialLink(
    val name: String,
    val url: String,
    val icon: String
)

