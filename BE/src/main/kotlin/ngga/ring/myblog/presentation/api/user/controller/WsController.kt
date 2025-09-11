package ngga.ring.myblog.presentation.api.user.controller

import ngga.ring.myblog.data.primary.db.entity.MessageEntity
import ngga.ring.myblog.data.primary.service.ChatService
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.stereotype.Controller
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import java.util.UUID

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic")
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS()
    }
}

@Controller
class WsController(private val chatService: ChatService) {

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    fun handle(@Payload msg: Map<String, Any>): MessageEntity {
        val roomId = UUID.fromString(msg["roomId"] as String)
        val senderId = UUID.fromString(msg["senderId"] as String)
        val content = msg["content"] as String
        return chatService.sendMessage(roomId, senderId, content)
    }
}