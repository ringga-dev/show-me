package ngga.ring.myblog.presentation.api.user.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ngga.ring.myblog.data.primary.service.BlogService
import ngga.ring.myblog.presentation.api.Config
import ngga.ring.myblog.presentation.api.user.dto.BlogRequest
import java.util.UUID

@Validated
@RestController
@RequestMapping("${Config.V1.URL}/blog")
class BlogController(
    private val blogService: BlogService
) {

    /* =========================
     *           LIST
     * ========================= */
    @GetMapping
    fun list(
        @RequestParam(defaultValue = "0") @Min(0) page: Int,
        @RequestParam(defaultValue = "10") @Min(1) @Max(200) size: Int,
        @RequestParam(required = false) q: String?,
        @RequestParam(required = false) published: Boolean?,
        @RequestParam(required = false) active: Boolean?
    ): ResponseEntity<Any> {
        val data = blogService.list(page = page, size = size, q = q, published = published, active = active)
        return ResponseEntity.ok(data)
    }

    /* =========================
     *       GET BY SLUG
     * ========================= */
    @GetMapping("/{slug}/slug")
    fun getBySlug(@PathVariable slug: String): ResponseEntity<Any> {
        val data = blogService.findBySlug(slug)
        return ResponseEntity.ok(data)
    }

    /* =========================
     *        GET BY ID
     * ========================= */
    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<Any> {
        val data = blogService.findById(id)
        return ResponseEntity.ok(data)
    }

    /* =========================
     *          CREATE
     * ========================= */
    @PostMapping
    fun create(@Valid @RequestBody req: BlogRequest): ResponseEntity<Any> {
        val data = blogService.addBlog(
            title = req.title,
            slug = req.slug,
            excerpt = req.excerpt,
            content = req.content,
            coverImage = req.coverImage,
            published = req.published,
            metaTitle = req.metaTitle,
            metaDescription = req.metaDescription,
            isActive = req.isActive,
            categories = req.categories,
            tags = req.tags
        )
        return ResponseEntity.ok(data)
    }

    /* =========================
     *          UPDATE
     * ========================= */
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody req: BlogRequest
    ): ResponseEntity<Any> {
        val data = blogService.updateBlog(
            id = id,
            title = req.title,
            slug = req.slug,
            excerpt = req.excerpt,
            content = req.content,
            coverImage = req.coverImage,
            published = req.published,
            metaTitle = req.metaTitle,
            metaDescription = req.metaDescription,
            isActive = req.isActive,
            categories = req.categories,
            tags = req.tags
        )
        return ResponseEntity.ok(data)
    }

    /* =========================
     *     PUBLISH / UNPUBLISH
     * ========================= */
    @PatchMapping("/{id}/publish")
    fun publish(
        @PathVariable id: String,
        @RequestParam(required = false) published: Boolean?= false
    ): ResponseEntity<Any> {
        val data = blogService.releseBlog(id = id, published = published)
        return ResponseEntity.ok(data)
    }

    /* =========================
     *     INCREMENT VIEW BY SLUG
     * ========================= */
    @PostMapping("/slug/{slug}/view")
    fun incrementView(@PathVariable slug: String): ResponseEntity<Any> {
        val data = blogService.incrementView(slug)
        return ResponseEntity.ok(data)
    }

    /* =========================
     *        SOFT DELETE
     * ========================= */
    @DeleteMapping("/{id}")
    fun softDelete(@PathVariable id: String): ResponseEntity<Any> {
        val data = blogService.deleteBlog(id)
        return ResponseEntity.ok(data)
    }

    /* =========================
     *          RESTORE
     * ========================= */
    @PostMapping("/{id}/restore")
    fun restore(@PathVariable id: String): ResponseEntity<Any> {
        val data = blogService.restore(id)
        return ResponseEntity.ok(data)
    }

    /* =========================
     *        HARD DELETE
     * ========================= */
    @DeleteMapping("/{id}/hard")
    fun hardDelete(@PathVariable id: String): ResponseEntity<Any> {
        val data = blogService.hardDelete(id)
        return ResponseEntity.ok(data)
    }

}
