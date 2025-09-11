package ngga.ring.myblog.presentation.api.public.controller

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import ngga.ring.myblog.data.primary.service.BlogService
import ngga.ring.myblog.data.primary.service.PromosionService
import ngga.ring.myblog.presentation.api.Config
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("${Config.V1.PUBLIC}")
class PublicController(
    private val promosionService: PromosionService,
    private val blogService: BlogService
) {
    @GetMapping("/promotion/all")
    fun findAllPromosion(): ResponseEntity<Any> {
        return ResponseEntity.ok(promosionService.findAllPromosion())
    }

    @GetMapping("/blogs")
    fun findAllBlogs(
        @RequestParam(defaultValue = "0") @Min(0) page: Int,
        @RequestParam(defaultValue = "10") @Min(1) @Max(200) size: Int,
        @RequestParam(required = false) q: String?,
        @RequestParam(required = false) published: Boolean?,
        @RequestParam(required = false) active: Boolean?
    ): ResponseEntity<Any> {
        val data = blogService.list(page = page, size = size, q = q, published = published, active = active)
        return ResponseEntity.ok(data)
    }


    @GetMapping("/blog/{slug}")
    fun findBlogBySlug(
        @PathVariable("slug") slug: String
    ): ResponseEntity<Any> {
        val data = blogService.findBySlug(slug)
        return ResponseEntity.ok(data)
    }
}