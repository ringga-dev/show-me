package ngga.ring.myblog.data.primary.repositories


import ngga.ring.myblog.data.primary.db.entity.UserPortfolioExperienceEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserPortfolioExperienceRepository: JpaRepository<UserPortfolioExperienceEntity, UUID> {
}