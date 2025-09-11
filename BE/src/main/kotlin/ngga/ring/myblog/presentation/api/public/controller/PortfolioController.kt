package ngga.ring.myblog.presentation.api.public.controller

import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.core.config.error.DataNotFoundException
import ngga.ring.myblog.data.primary.service.FileService
import ngga.ring.myblog.data.primary.service.PortfolioService
import ngga.ring.myblog.presentation.api.Config
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.nio.file.Files

@RestController("publicPortfolioController")
@RequestMapping("${Config.V1.PUBLIC}/portfolio")
class PortfolioController(
    private val portfolioService: PortfolioService
) {

    @GetMapping
    fun getPortfolio(): ResponseEntity<Any> {
        return ResponseEntity.ok(portfolioService.getPortfolio())
    }

    @GetMapping("/project")
    fun getPortfolioProject(): ResponseEntity<Any> {
        return ResponseEntity.ok(portfolioService.getPortfolioProject())
    }

    @GetMapping("/skill")
    fun getPortfolioSkill(): ResponseEntity<Any> {
        return ResponseEntity.ok(portfolioService.getPortfolioSkill())
    }

    @GetMapping("/experience")
    fun getPortfolioExperience(): ResponseEntity<Any> {
        return ResponseEntity.ok(portfolioService.getPortfolioExperience())
    }

}

