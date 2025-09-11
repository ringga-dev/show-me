package ngga.ring.myblog.data.primary.repositories

import ngga.ring.myblog.data.primary.db.entity.RoomEntity
import ngga.ring.myblog.data.primary.db.entity.RoomMemberEntity
import ngga.ring.myblog.data.primary.db.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

interface RoomMemberRepository : JpaRepository<RoomMemberEntity, UUID> {
    fun findByRoomAndUser(room: RoomEntity, user: UserEntity): Optional<RoomMemberEntity>
    fun findAllByUser(user: UserEntity): List<RoomMemberEntity>
    fun countByRoom(room: RoomEntity): Long

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("DELETE FROM RoomMemberEntity rm WHERE rm.user.userId = :userId")
    fun deleteByUserId(userId: UUID): Int
}