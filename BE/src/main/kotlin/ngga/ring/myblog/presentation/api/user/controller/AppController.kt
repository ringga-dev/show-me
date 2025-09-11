package ngga.ring.myblog.presentation.api.user.controller

import io.swagger.v3.oas.annotations.Operation
import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.data.auth.service.AuthService
import ngga.ring.myblog.data.primary.models.AdsModel
import ngga.ring.myblog.data.primary.models.FeatureModel
import ngga.ring.myblog.data.primary.service.AppService
import ngga.ring.myblog.data.primary.service.UserService
import ngga.ring.myblog.presentation.api.Config
import ngga.ring.myblog.presentation.api.auth.dto.ForgotPasswordRequest
import ngga.ring.myblog.presentation.api.auth.dto.LoginRequest
import ngga.ring.myblog.presentation.api.auth.dto.RefreshTokenRequest
import ngga.ring.myblog.presentation.api.auth.dto.RegisterRequest
import ngga.ring.myblog.presentation.api.user.dto.AppDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.String


@RestController
@RequestMapping("${Config.V1.URL}/app")
class AppController(
    private val appService: AppService
) {
    @Operation(summary = "get all data", description = "Get All data")
    @GetMapping("/all")
    fun getAllApps(): ResponseEntity<Any> {
        return ResponseEntity.ok(appService.getAllApp())
    }

    @Operation(summary = "by id", description = "getdata by id")
    @GetMapping("/{id}")
    fun getAppById(
        @PathVariable("id") id: String
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(appService.getAppById(id))
    }

    @Operation(summary = "by slug", description = "getdata by slug")
    @GetMapping("/{slug}/slug")
    fun getAppBySlug(
        @PathVariable("slug") slug: String
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(appService.getAppBySlug(slug))
    }

    @Operation(summary = "add data", description = "add data")
    @PostMapping("/add")
    fun addApp(@RequestBody body: AppDto): ResponseEntity<Any> {
        return ResponseEntity.ok(
            appService.addApp(
                name = body.name,
                slug = body.slug,
                image = body.image,
                description = body.description,
                isActive = body.isActive,
                features = body.features,
                ads = body.ads,
            )
        )
    }

    @Operation(summary = "update data", description = "update data")
    @PutMapping("/{id}")
    fun updateApp(
        @PathVariable("id") id: String,
        @RequestBody body: AppDto
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(
            appService.updateApp(
                id = id,
                name = body.name,
                slug = body.slug,
                image = body.image,
                description = body.description,
                isActive = body.isActive,
                features = body.features,
                ads = body.ads,
            )
        )
    }

    @Operation(summary = "delete data", description = "delete data")
    @DeleteMapping("/{id}")
    fun deleteApp(
        @PathVariable("id") id: String
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(appService.deleteApp(id))
    }



}


