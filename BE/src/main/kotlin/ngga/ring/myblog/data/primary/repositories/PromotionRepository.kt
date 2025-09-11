package ngga.ring.myblog.data.primary.repositories

import ngga.ring.myblog.data.primary.db.entity.PromotionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PromotionRepository : JpaRepository<PromotionEntity, UUID> {

}