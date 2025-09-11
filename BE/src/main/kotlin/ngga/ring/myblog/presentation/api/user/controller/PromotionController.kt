package ngga.ring.myblog.presentation.api.user.controller

import ngga.ring.myblog.data.primary.db.entity.TypeStatus
import ngga.ring.myblog.data.primary.service.PromosionService
import ngga.ring.myblog.presentation.api.Config
import ngga.ring.myblog.presentation.api.user.dto.AppDto
import ngga.ring.myblog.presentation.api.user.dto.PromotionDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("${Config.V1.URL}/promosion")
class PromotionController(private val promosionService: PromosionService) {
    @GetMapping("/all")
    fun findAllPromosion(): ResponseEntity<Any> {
        return ResponseEntity.ok(promosionService.findAllPromosion())
    }

    @PostMapping("/add")
    fun insertPromosion(
        @RequestBody body: PromotionDto
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(
            promosionService.insertPromosion(
                name = body.name,
                description = body.description,
                rating = body.rating,
                type = body.type,
                url = body.url,
                pos = body.pos,
            )
        )
    }

    @GetMapping("/{id}/update")
    fun updatePromosion(
        @PathVariable("id") id: String,
        @RequestBody body: PromotionDto
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(
            promosionService.updatePromosion(
                id = id,
                name = body.name,
                description = body.description,
                rating = body.rating,
                type = body.type,
                url = body.url,
                pos = body.pos,
            )
        )
    }

    @DeleteMapping("/{id}/delete")
    fun deletePromosion(id: String): ResponseEntity<Any> {
        return ResponseEntity.ok(promosionService.deletePromosion(id))
    }
}