package ngga.ring.myblog.presentation.api.user.controller

import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.core.config.error.DataNotFoundException
import ngga.ring.myblog.data.primary.service.FileService
import ngga.ring.myblog.data.primary.service.PortfolioService
import ngga.ring.myblog.presentation.api.Config
import ngga.ring.myblog.presentation.api.user.dto.AppDto
import ngga.ring.myblog.presentation.api.user.dto.PortfolioDto
import ngga.ring.myblog.presentation.api.user.dto.PortfolioExperienceDto
import ngga.ring.myblog.presentation.api.user.dto.PortfolioProjectDto
import ngga.ring.myblog.presentation.api.user.dto.PortfolioSkillDto
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
import java.time.LocalDateTime
import kotlin.String
import kotlin.collections.List

@RestController("userPortfolioController")
@RequestMapping("${Config.V1.URL}/portfolio")
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

    @GetMapping("/project/{id}")
    fun getPortfolioProjectById(
        @PathVariable("id") id: String
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(portfolioService.getPortfolioProjectById(id))
    }


    @GetMapping("/skill")
    fun getPortfolioSkill(): ResponseEntity<Any> {
        return ResponseEntity.ok(portfolioService.getPortfolioSkill())
    }

    @GetMapping("/skill/{id}")
    fun getPortfolioSkillById(
        @PathVariable("id") id: String
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(portfolioService.getPortfolioSkillById(id))
    }

    @GetMapping("/experience")
    fun getPortfolioExperience(): ResponseEntity<Any> {
        return ResponseEntity.ok(portfolioService.getPortfolioExperience())
    }

    @GetMapping("/experience/{id}")
    fun getPortfolioExperienceById(
        @PathVariable("id") id: String
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(portfolioService.getPortfolioExperienceById(id))
    }

    @PostMapping
    fun updatePortfolio(
        @RequestBody body: PortfolioDto
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(
            portfolioService.updateUserPortfolio(
                name = body.name,
                image = body.image,
                email = body.email,
                phone = body.phone,
                address = body.address,
                titles = body.titles,
                description = body.description,
                hashTag = body.hashTag,
                socialMedia = body.socialMedia,
                workedWith = body.workedWith,
                aboutMe = body.aboutMe,
            )
        )
    }

    @PostMapping("/project")
    fun addProject(
        @RequestBody body: PortfolioProjectDto
    ): BaseResponse<Any> {
        return portfolioService.addPortfolioProject(
            title = body.title,
            imageUrl = body.imageUrl,
            githubUrl = body.githubUrl,
            demoUrl = body.demoUrl,
            description = body.description,
            technologies = body.technologies,
            dateCreated = body.dateCreated,
            userRequest = body.userRequest,
            rating = body.rating,
            releseVersion = body.releseVersion
        )
    }

    @PostMapping("/skill")
    fun addSkill(
        @RequestBody body: PortfolioSkillDto
    ): BaseResponse<Any> {
        return portfolioService.addPortfolioSkill(
            title = body.title,
            description = body.description,
            icon = body.icon,
            skillData = body.skillData
        )
    }

    @PostMapping("/experience")
    fun addExperience(
        @RequestBody body: PortfolioExperienceDto
    ): BaseResponse<Any> {
        return portfolioService.addPortfolioExperience(
            title = body.title,
            company = body.company,
            location = body.location,
            startDate = body.startDate,
            endDate = body.endDate,
            description = body.description,
            technologies = body.technologies,
            highlights= body.highlights,
        )
    }

    @PutMapping("/project/{id}")
    fun updateProject(
        @PathVariable("id") id: String,
        @RequestBody body: PortfolioProjectDto
    ): BaseResponse<Any> {
        return portfolioService.updatePortfolioProject(
            id = id,
            title = body.title,
            imageUrl = body.imageUrl,
            githubUrl = body.githubUrl,
            demoUrl = body.demoUrl,
            description = body.description,
            technologies = body.technologies,
            dateCreated = body.dateCreated,
            userRequest = body.userRequest,
            rating = body.rating,
            releseVersion = body.releseVersion
        )
    }

    @PutMapping("/skill/{id}")
    fun updateSkill(
        @PathVariable("id") id: String,
        @RequestBody body: PortfolioSkillDto
    ): BaseResponse<Any> {
        return portfolioService.updatePortfolioSkill(
            id = id,
            title = body.title,
            icon= body.icon,
            description = body.description,
            skillData = body.skillData
        )
    }

    @PutMapping("/experience/{id}")
    fun updateExperience(
        @PathVariable("id") id: String,
        @RequestBody body: PortfolioExperienceDto
    ): BaseResponse<Any> {
        return portfolioService.updatePortfolioExperience(
            id = id,
            title = body.title,
            company = body.company,
            location = body.location,
            startDate = body.startDate,
            endDate = body.endDate,
            description = body.description,
            technologies = body.technologies
        )
    }

    @DeleteMapping("/project/{id}")
    fun deleteProject(
        @PathVariable("id") id: String
    ): BaseResponse<Any> {
        return portfolioService.deletePortfolioProject(
            id = id
        )
    }

    @DeleteMapping("/skill/{id}")
    fun deleteSkill(
        @PathVariable("id") id: String
    ): BaseResponse<Any> {
        return portfolioService.deletePortfolioSkill(
            id = id
        )
    }

    @DeleteMapping("/experience/{id}")
    fun deleteExperience(
        @PathVariable("id") id: String
    ): BaseResponse<Any> {
        return portfolioService.deletePortfolioExperience(
            id = id
        )
    }
}
