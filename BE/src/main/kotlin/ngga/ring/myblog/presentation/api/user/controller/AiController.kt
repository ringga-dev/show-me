package ngga.ring.myblog.presentation.api.user.controller

import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.data.primary.service.AiService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/ai")
class AiController(private val aiService: AiService) {
//    @PostMapping("/gemini-send")
//    fun aiGeminiSend(@RequestParam promt: String): ResponseEntity<Any> {
//        return ResponseEntity.ok(aiService.aiGeminiSend(promt))
//    }

    @PostMapping("/gemini-send-aks")
    fun aiGeminiAksSend(@RequestParam promt: String): ResponseEntity<Any> {
        return ResponseEntity.ok(aiService.aiGeminiAksSend(promt))
    }
}