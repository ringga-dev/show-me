package ngga.ring.myblog.presentation.api.user.controller

import ngga.ring.myblog.data.primary.service.ChatService
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/chat")
class ChatController(private val chatService: ChatService) {

    @PostMapping("/group")
    fun createGroup(
        @RequestParam name: String,
        @RequestParam creatorId: UUID,
        @RequestBody memberIds: Set<UUID>
    ) = chatService.createGroup(name, creatorId, memberIds)

    @PostMapping("/dm")
    fun startDm(
        @RequestParam userA: UUID,
        @RequestParam userB: UUID
    ) = chatService.findOrCreateDm(userA, userB)

    @PostMapping("/message")
    fun send(
        @RequestParam roomId: UUID,
        @RequestParam senderId: UUID,
        @RequestParam content: String
    ) = chatService.sendMessage(roomId, senderId, content)

    @GetMapping("/message/{roomId}")
    fun load(
        @PathVariable roomId: UUID,
        pageable: Pageable
    ) = chatService.loadMessages(roomId, pageable)
}