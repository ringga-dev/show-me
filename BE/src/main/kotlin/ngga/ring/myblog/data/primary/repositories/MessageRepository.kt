package ngga.ring.myblog.data.primary.repositories

import ngga.ring.myblog.data.primary.db.entity.MessageEntity
import ngga.ring.myblog.data.primary.db.entity.RoomEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MessageRepository : JpaRepository<MessageEntity, UUID> {
    fun findAllByRoomOrderBySentAtAsc(room: RoomEntity, pageable: Pageable): Page<MessageEntity>
}