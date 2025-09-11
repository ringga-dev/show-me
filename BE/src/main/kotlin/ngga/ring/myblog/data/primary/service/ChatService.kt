package ngga.ring.myblog.data.primary.service

import ngga.ring.myblog.data.primary.db.entity.*
import ngga.ring.myblog.data.primary.repositories.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ChatService(
    private val roomRepo: RoomRepository,
    private val memberRepo: RoomMemberRepository,
    private val msgRepo: MessageRepository,
    private val userRepo: UserRepository
) {

    /* 1. Buat grup + set creator sebagai OWNER */
    @Transactional
    fun createGroup(name: String, creatorId: UUID, memberIds: Set<UUID>): RoomEntity {
        val creator = userRepo.findById(creatorId)
            .orElseThrow { NoSuchElementException("creator not found") }

        val room = roomRepo.save(
            RoomEntity(
                name = name,
                isGroup = true,
                createdBy = creatorId
            )
        )

        // creator = OWNER, sisanya MEMBER
        (memberIds + creatorId).toSet().forEach { uid ->
            val user = userRepo.findById(uid)
                .orElseThrow { NoSuchElementException("user $uid") }

            val role = if (uid == creatorId) RoomRole.OWNER else RoomRole.MEMBER
            memberRepo.save(RoomMemberEntity(room = room, user = user, role = role))
        }
        return room
    }

    /* 2. DM tetap tanpa role (atau bisa juga kasih default MEMBER) */
    @Transactional
    fun findOrCreateDm(userA: UUID, userB: UUID): RoomEntity {
        val dm = roomRepo.findDmBetween(listOf(userA, userB))
        if (dm.isPresent) return dm.get()

        val room = roomRepo.save(
            RoomEntity(
                name = "DM-$userA-$userB",
                isGroup = false,
                createdBy = userA
            )
        )

        listOf(userA, userB).forEach { uid ->
            val user = userRepo.findById(uid).orElseThrow()
            // DM tidak perlu role detail; boleh default MEMBER
            memberRepo.save(RoomMemberEntity(room = room, user = user, role = RoomRole.MEMBER))
        }
        return room
    }

    /* 3. Kirim pesan (cek apakah sender adalah anggota) */
    @Transactional
    fun sendMessage(roomId: UUID, senderId: UUID, content: String): MessageEntity {
        val room = roomRepo.findById(roomId).orElseThrow()
        val sender = userRepo.findById(senderId).orElseThrow()

        val membership = memberRepo.findByRoomAndUser(room, sender)
            .orElseThrow { IllegalStateException("sender not in room") }

        return msgRepo.save(
            MessageEntity(
                room = room,
                sender = sender,
                content = content
            )
        )
    }

    /* 4. Ambil pesan suatu room */
    fun loadMessages(roomId: UUID, pageable: Pageable): Page<MessageEntity> {
        val room = roomRepo.findById(roomId).orElseThrow()
        return msgRepo.findAllByRoomOrderBySentAtAsc(room, pageable)
    }

    /* 5. Helper untuk mengecek role (bisa dipakai di controller lain) */
    fun getUserRole(roomId: UUID, userId: UUID): RoomRole? =
        roomRepo.findById(roomId)
            .flatMap { room -> memberRepo.findByRoomAndUser(room, userRepo.getById(userId)) }
            .map { it.role }
            .orElse(null)

    fun hasRole(roomId: UUID, userId: UUID, role: RoomRole): Boolean =
        getUserRole(roomId, userId) == role
}