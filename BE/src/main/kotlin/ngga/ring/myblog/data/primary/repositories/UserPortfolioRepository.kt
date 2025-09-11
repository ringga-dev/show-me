package ngga.ring.myblog.data.primary.repositories

import jakarta.persistence.LockModeType
import ngga.ring.myblog.data.primary.db.entity.UserPortfolioEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface UserPortfolioRepository: JpaRepository<UserPortfolioEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserPortfolioEntity u where u.id = :id")
    fun lockById(id: UUID): UserPortfolioEntity?
}