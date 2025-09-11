package ngga.ring.myblog.data.primary.db.entity


import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*

/* ===== ROOM / GRUP ===== */
@Entity
@Table(name = "t_rooms")
data class RoomEntity(
    @Id
    @Column(name = "room_id", updatable = false, nullable = false)
    val roomId: UUID = UUID.randomUUID(),

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "is_group", nullable = false)
    val isGroup: Boolean = false,          // false = DM, true = Grup


    @Column(name = "created_by", nullable = false)
    val createdBy: UUID,                   // userId pembuat grup

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)


