package ngga.ring.myblog.presentation.api.user.controller

import jakarta.validation.Valid
import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.core.config.error.ResourceNotFoundException
import ngga.ring.myblog.data.primary.repositories.TokenAppsRepository
import ngga.ring.myblog.data.primary.service.TokenAppsService
import ngga.ring.myblog.presentation.api.Config
import ngga.ring.myblog.presentation.api.user.dto.TokenDto
import org.springframework.data.domain.Page
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.awt.print.Pageable
import java.net.URI

/* ================================
 * Controller
 * ================================ */
@RestController
@RequestMapping("${Config.V1.URL}/token")
class TokenAppsController(
    private val service: TokenAppsService,
) {

    /* CREATE */
    @PostMapping
    fun create(@Valid @RequestBody req: TokenDto): ResponseEntity<Any> {
        val saved = service.create(
            name = req.name,
            token = req.token,
            quota = req.quota,
            usageCount = req.usageCount,
            isActive = req.isActive,
            expiredAt = req.expiredAt,
            note = req.note
        )
        return ResponseEntity.ok(saved)
    }

    /* READ ALL */
    @GetMapping
    fun getAll(): ResponseEntity<Any> {
        val found = service.getAll()
        return ResponseEntity.ok(found)
    }

    /* READ BY ID */
    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ResponseEntity<Any> {
        val found = service.getById(id)
        return ResponseEntity.ok(found)
    }


    /* UPDATE (PUT = replace) */
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody req: TokenDto
    ): ResponseEntity<Any> {
        // bangun entity “baru” lalu serahkan ke service.update

        val updated = service.update(  id = id, // penting: id diisi agar copy() di service konsisten
            name = req.name,
            token = req.token,
            quota = req.quota,
            usageCount = req.usageCount,
            isActive = req.isActive,
            expiredAt = req.expiredAt,
            note = req.note
        )
        return ResponseEntity.ok(updated)
    }

    /* DELETE */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Any> {
        val ok = service.delete(id)
        return ResponseEntity.ok(ok)
    }


}