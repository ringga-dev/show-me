package ngga.ring.myblog.data.primary.repositories

import jakarta.persistence.LockModeType
import ngga.ring.myblog.data.primary.db.entity.UserPortfolioEntity
import ngga.ring.myblog.data.primary.db.entity.UserPortfolioProjectEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface UserPortfolioProjectRepository: JpaRepository<UserPortfolioProjectEntity, UUID> {
}