package ngga.ring.myblog.data.primary.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import ngga.ring.myblog.core.converter.SkillDataListConverter
import ngga.ring.myblog.core.converter.StringListConverter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(name = "m_user_portfolio_skills")
data class UserPortfolioSkillEntity(
    @Id
    @Column(name = "user_portfolio_skill_id", updatable = false, nullable = false)
    var id: UUID? = UUID.randomUUID(),

    @Column(name = "title", nullable = false)
    var title: String? = null,

    @Column(name = "description", nullable = true, columnDefinition = "LONGTEXT")
    var description: String? = null,

    @Column(name = "icon", nullable = true, columnDefinition = "LONGTEXT")
    var icon: String? = null,

    @Convert(converter = SkillDataListConverter::class)
    @Column(name = "skill_data", nullable = false, columnDefinition = "LONGTEXT")
    var skillData: List<SkillData>? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = true)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

data class SkillData(
    val name: String,
    val percentage: Int
)
