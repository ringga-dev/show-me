package ngga.ring.myblog.data.primary.repositories

import ngga.ring.myblog.data.primary.db.entity.UserPortfolioSkillEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserPortfolioSkillRepository: JpaRepository<UserPortfolioSkillEntity, UUID> {
}