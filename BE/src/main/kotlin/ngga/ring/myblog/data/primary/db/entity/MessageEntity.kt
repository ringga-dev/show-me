package ngga.ring.myblog.data.primary.db.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "t_messages")
data class MessageEntity(
    @Id
    @Column(name = "message_id", updatable = false, nullable = false)
    val messageId: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    val room: RoomEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: UserEntity,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    var content: String,

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    val sentAt: LocalDateTime = LocalDateTime.now()
)