package ngga.ring.myblog.data.primary.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import ngga.ring.myblog.core.converter.StringListConverter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(name = "m_user_portfolio_project")
data class UserPortfolioProjectEntity(
    @Id
    @Column(name = "user_portfolio_project_id", updatable = false, nullable = false)
    var id: UUID? = UUID.randomUUID(),

    @Column(name = "title", nullable = false, columnDefinition = "LONGTEXT")
    var title: String? = null,

    @Column(name = "image_url", nullable = true,  columnDefinition = "LONGTEXT")
    var imageUrl: String? = null,

    @Column(name = "github_url", nullable = true,  columnDefinition = "LONGTEXT")
    var githubUrl: String? = null,

    @Column(name = "demo_url", nullable = true,  columnDefinition = "LONGTEXT")
    var demoUrl: String? = null,

    @Column(name = "description", nullable = true,  columnDefinition = "LONGTEXT")
    var description: String? = null,

    @Column(name = "technologies", nullable = true, columnDefinition = "LONGTEXT")
    var technologies: List<String>? = null,

    @Column(name = "date_created", nullable = true)
    var dateCreated: LocalDateTime? = null,

    @Column(name = "user_request", nullable = true)
    var userRequest: Int? = null,

    @Column(name = "rating", nullable = true)
    var rating: Int? = null,

    @Column(name = "relese_version", nullable = true)
    var releseVersion: String? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

