package ngga.ring.myblog.data.primary.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import ngga.ring.myblog.core.converter.StringListConverter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(name = "m_user_portfolio_experience")
data class UserPortfolioExperienceEntity(
    @Id
    @Column(name = "user_portfolio_project_id", updatable = false, nullable = false)
    var id: UUID? = UUID.randomUUID(),

    @Column(name = "title", nullable = false)
    var title: String? = null,

    @Column(name = "company", nullable = false)
    var company: String? = null,

    @Column(name = "location", nullable = false)
    var location: String? = null,

    @Column(name = "start_date", nullable = false)
    var startDate: String? = null,

    @Column(name = "end_date", nullable = true)
    var endDate: String? = null,

    @Column(name = "description", nullable = true,  columnDefinition = "LONGTEXT")
    var description: String? = null,

    @Convert(converter = StringListConverter::class)
    @Column(name = "technologies", nullable = true, columnDefinition = "LONGTEXT")
    var technologies: List<String>? = null,

    @Convert(converter = StringListConverter::class)
    @Column(name = "highlights", nullable = true, columnDefinition = "LONGTEXT")
    var highlights: List<String>? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

