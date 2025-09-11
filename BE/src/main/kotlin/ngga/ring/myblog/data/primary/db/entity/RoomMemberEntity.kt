package ngga.ring.myblog.data.primary.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "t_room_members",
    uniqueConstraints = [UniqueConstraint(columnNames = ["room_id", "user_id"])]
)
data class RoomMemberEntity(
    @Id
    @Column(name = "room_member_id", updatable = false, nullable = false)
    val roomMemberId: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    val room: RoomEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    val role: RoomRole = RoomRole.MEMBER,

    @Column(name = "joined_at", updatable = false)
    val joinedAt: LocalDateTime = LocalDateTime.now()
)

enum class RoomRole { OWNER, ADMIN, MEMBER }
