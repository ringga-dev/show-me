package ngga.ring.myblog.data.primary.repositories


import ngga.ring.myblog.data.primary.db.entity.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface RoomRepository : JpaRepository<RoomEntity, UUID> {
    /* cari DM antara dua user (kalau sudah ada) */
    @Query("""
        SELECT r FROM RoomEntity r
        WHERE r.isGroup = false
          AND r.roomId IN (
              SELECT rm.room.roomId FROM RoomMemberEntity rm
              WHERE rm.user.userId IN :userIds
              GROUP BY rm.room.roomId
              HAVING COUNT(DISTINCT rm.user.userId) = 2
          )
    """)
    fun findDmBetween(userIds: List<UUID>): Optional<RoomEntity>
}



