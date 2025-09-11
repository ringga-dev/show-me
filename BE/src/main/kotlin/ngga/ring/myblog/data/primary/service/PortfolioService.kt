package ngga.ring.myblog.data.primary.service

import jakarta.persistence.Column
import jakarta.persistence.Convert
import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.core.config.error.DataNotFoundException
import ngga.ring.myblog.core.converter.SkillDataListConverter
import ngga.ring.myblog.core.converter.StringListConverter
import ngga.ring.myblog.data.primary.db.entity.SkillData
import ngga.ring.myblog.data.primary.db.entity.SocialLink
import ngga.ring.myblog.data.primary.db.entity.UserPortfolioEntity
import ngga.ring.myblog.data.primary.db.entity.UserPortfolioExperienceEntity
import ngga.ring.myblog.data.primary.db.entity.UserPortfolioProjectEntity
import ngga.ring.myblog.data.primary.db.entity.UserPortfolioSkillEntity
import ngga.ring.myblog.data.primary.repositories.UserPortfolioExperienceRepository
import ngga.ring.myblog.data.primary.repositories.UserPortfolioProjectRepository
import ngga.ring.myblog.data.primary.repositories.UserPortfolioRepository
import ngga.ring.myblog.data.primary.repositories.UserPortfolioSkillRepository
import org.springframework.http.HttpStatus
import org.springframework.context.annotation.Description
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID


interface PortfolioService {
    fun updateUserPortfolio(
        name: String? = null,
        image: String? = null,
        email: String? = null,
        phone: String? = null,
        address: String? = null,
        titles: List<String>? = null,
        description: String? = null,
        hashTag: List<String>? = null,
        socialMedia: List<SocialLink>? = null,
        workedWith: List<String>? = null,
        aboutMe: String? = null,
    ): BaseResponse<Any>

    fun getPortfolio(): BaseResponse<Any>

    fun getPortfolioProject(): BaseResponse<Any>
    fun getPortfolioProjectById(id: String): BaseResponse<Any>
    fun getPortfolioSkill(): BaseResponse<Any>
    fun getPortfolioSkillById(id: String): BaseResponse<Any>
    fun getPortfolioExperience(): BaseResponse<Any>
    fun getPortfolioExperienceById(id: String): BaseResponse<Any>

    fun addPortfolioProject(
        title: String? = null,
        imageUrl: String? = null,
        githubUrl: String? = null,
        demoUrl: String? = null,
        description: String? = null,
        technologies: List<String>? = null,
        dateCreated: LocalDateTime? = null,
        userRequest: Int? = null,
        rating: Int? = null,
        releseVersion: String? = null
    ): BaseResponse<Any>

    fun addPortfolioSkill(
        title: String? = null,
        description: String? = null,
        icon: String? = null,
        skillData: List<SkillData>? = null,
    ): BaseResponse<Any>

    fun addPortfolioExperience(
        title: String? = null,
        company: String? = null,
        location: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        description: String? = null,
        technologies: List<String>? = null,
        highlights: List<String>? = null
    ): BaseResponse<Any>

    fun updatePortfolioProject(
        id: String,
        title: String? = null,
        imageUrl: String? = null,
        githubUrl: String? = null,
        demoUrl: String? = null,
        description: String? = null,
        technologies: List<String>? = null,
        dateCreated: LocalDateTime? = null,
        userRequest: Int? = null,
        rating: Int? = null,
        releseVersion: String? = null
    ): BaseResponse<Any>

    fun updatePortfolioSkill(
        id: String,
        title: String? = null,
        description: String? = null,
        icon: String? = null,
        skillData: List<SkillData>? = null,
    ): BaseResponse<Any>

    fun updatePortfolioExperience(
        id: String,
        title: String? = null,
        company: String? = null,
        location: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        description: String? = null,
        technologies: List<String>? = null,
        highlights: List<String>? = null
    ): BaseResponse<Any>

    fun deletePortfolioProject(
        id: String
    ): BaseResponse<Any>

    fun deletePortfolioSkill(
        id: String
    ): BaseResponse<Any>

    fun deletePortfolioExperience(
        id: String
    ): BaseResponse<Any>

}


@Service
class PortfolioServiceImpl(
    private val fileService: FileService,
    private val userPortfolioRepository: UserPortfolioRepository,
    private val userPortfolioProjectRepository: UserPortfolioProjectRepository,
    private val userPortfolioSkillRepository: UserPortfolioSkillRepository,
    private val userPortfolioExperienceRepository: UserPortfolioExperienceRepository,
) : PortfolioService {


    companion object {
        /**
         * UUID tetap untuk baris tunggal
         */
        val SINGLETON_ID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")
    }

    /** Utility kecil untuk "sanitize" list: hapus null/blank, trim. */
    private fun sanitize(list: List<String?>?): List<String>? =
        list?.mapNotNull { it?.trim() }?.filter { it.isNotEmpty() }?.ifEmpty { emptyList() }


    @Transactional
    override fun updateUserPortfolio(
        name: String?,
        image: String?,
        email: String?,
        phone: String?,
        address: String?,
        titles: List<String>?,
        description: String?,
        hashTag: List<String>?,
        socialMedia: List<SocialLink>?,
        workedWith: List<String>?,
        aboutMe: String?
    ): BaseResponse<Any> = try {

        val entity = userPortfolioRepository.lockById(SINGLETON_ID)
            ?: UserPortfolioEntity(id = SINGLETON_ID)

        fun sanitizeStrings(list: List<String>?): List<String>? =
            list?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?.distinct()

        fun sanitizeLinks(list: List<SocialLink>?): List<SocialLink>? =
            list?.map { it.copy(name = it.name.trim(), url = it.url.trim(), icon = it.icon.trim()) }
                ?.filter { it.name.isNotBlank() && it.url.isNotBlank() }
                ?.distinctBy { it.name.lowercase() to it.url.lowercase() }

        name?.let { entity.name = it.trim() }
        image?.let { entity.image = it.trim() }
        email?.let { entity.email = it.trim() }
        phone?.let { entity.phone = it.trim() }
        address?.let { entity.address = it.trim() }
        titles?.let { entity.titles = sanitizeStrings(it) }
        description?.let { entity.description = it } // biarkan HTML apa adanya (sanitize saat render)
        hashTag.let { entity.hashTag = sanitizeStrings(it) }
        socialMedia?.let { entity.socialMedia = sanitizeLinks(it) }
        workedWith?.let { entity.workedWith = sanitizeStrings(it) }
        aboutMe?.let { entity.aboutMe = it } // HTML

        val saved = userPortfolioRepository.save(entity)

        BaseResponse(
            code = HttpStatus.OK.value(),
            status = HttpStatus.OK,
            message = arrayListOf("Update Success"),
            data = saved
        )
    } catch (ex: Exception) {
        BaseResponse(
            code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            message = arrayListOf(
                "Gagal update user portfolio: ${ex.message}"
            )
        )
    }


    @Transactional
    override fun getPortfolio(): BaseResponse<Any> {
        return try {
            val entity = userPortfolioRepository.findById(SINGLETON_ID)
                .orElse(UserPortfolioEntity(id = SINGLETON_ID))
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
                data = entity
            )
        } catch (ex: Exception) {
            BaseResponse(
                code = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                status = HttpStatus.INTERNAL_SERVER_ERROR,
                message = arrayListOf(
                    "Gagal update user portfolio: ${ex.message}"
                )
            )
        }
    }

    @Transactional
    override fun getPortfolioProject(): BaseResponse<Any> {
        try {
            val entity = userPortfolioProjectRepository.findAll()
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
                data = entity
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    override fun getPortfolioProjectById(id: String): BaseResponse<Any> {
        try {
            val entity = userPortfolioProjectRepository.findById(UUID.fromString(id))
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
                data = entity
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    @Transactional
    override fun getPortfolioSkill(): BaseResponse<Any> {
        try {
            val entity = userPortfolioSkillRepository.findAll()
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
                data = entity
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    override fun getPortfolioSkillById(id: String): BaseResponse<Any> {
        try {
            val entity = userPortfolioSkillRepository.findById(UUID.fromString(id))
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
                data = entity
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    @Transactional
    override fun getPortfolioExperience(): BaseResponse<Any> {
        try {
            val entity = userPortfolioExperienceRepository.findAll()
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
                data = entity
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    override fun getPortfolioExperienceById(id: String): BaseResponse<Any> {
        try {
            val entity = userPortfolioExperienceRepository.findById(UUID.fromString(id))
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
                data = entity
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    @Transactional
    override fun addPortfolioProject(
        title: String?,
        imageUrl: String?,
        githubUrl: String?,
        demoUrl: String?,
        description: String?,
        technologies: List<String>?,
        dateCreated: LocalDateTime?,
        userRequest: Int?,
        rating: Int?,
        releseVersion: String?
    ): BaseResponse<Any> {
        try {
            val data = UserPortfolioProjectEntity(
                title = title,
                imageUrl = imageUrl,
                githubUrl = githubUrl,
                demoUrl = demoUrl,
                description = description,
                technologies = technologies,
                dateCreated = dateCreated,
                userRequest = userRequest,
                rating = rating,
                releseVersion = releseVersion
            )
            val saved = userPortfolioProjectRepository.save(data)
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
                data = saved
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    @Transactional
    override fun addPortfolioSkill(
        title: String?,
        description: String?,
        icon:String?,
        skillData: List<SkillData>?
    ): BaseResponse<Any> {
        try {
            val data = UserPortfolioSkillEntity(
                title = title,
                description = description,
                icon = icon,
                skillData = skillData
            )
            val saved = userPortfolioSkillRepository.save(data)
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
                data = saved
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    @Transactional
    override fun addPortfolioExperience(
        title: String?,
        company: String?,
        location: String?,
        startDate: String?,
        endDate: String?,
        description: String?,
        technologies: List<String>?,
        highlights: List<String>?
    ): BaseResponse<Any> {
        try {
            val data = UserPortfolioExperienceEntity(
                title = title,
                company = company,
                location = location,
                startDate = startDate,
                endDate = endDate,
                description = description,
                technologies = technologies,
                highlights = highlights
            )
            val saved = userPortfolioExperienceRepository.save(data)
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
                data = saved
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    @Transactional
    override fun updatePortfolioProject(
        id: String,
        title: String?,
        imageUrl: String?,
        githubUrl: String?,
        demoUrl: String?,
        description: String?,
        technologies: List<String>?,
        dateCreated: LocalDateTime?,
        userRequest: Int?,
        rating: Int?,
        releseVersion: String?
    ): BaseResponse<Any> {
        try {
            val entity = userPortfolioProjectRepository.findById(UUID.fromString(id)).orElseThrow {
                DataNotFoundException("User portfolio project not found")
            }
            entity.title = title
            entity.imageUrl = imageUrl
            entity.githubUrl = githubUrl
            entity.demoUrl = demoUrl
            entity.description = description
            entity.technologies = technologies
            entity.dateCreated = dateCreated
            entity.userRequest = userRequest
            entity.rating = rating
            entity.releseVersion = releseVersion
            val saved = userPortfolioProjectRepository.save(entity)
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
                data = saved
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    @Transactional
    override fun updatePortfolioSkill(
        id: String,
        title: String?,
        description: String?,
        icon: String?,
        skillData: List<SkillData>?,
    ): BaseResponse<Any> {
        try {
            val entity = userPortfolioSkillRepository.findById(UUID.fromString(id)).orElseThrow {
                DataNotFoundException("User portfolio skill not found")
            }
            entity.title = title
            entity.description = description
            entity.icon = icon
            entity.skillData = skillData
            val saved = userPortfolioSkillRepository.save(entity)
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
                data = saved
            )

        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    @Transactional
    override fun updatePortfolioExperience(
        id: String,
        title: String?,
        company: String?,
        location: String?,
        startDate: String?,
        endDate: String?,
        description: String?,
        technologies: List<String>?,
        highlights: List<String>?
    ): BaseResponse<Any> {
        try {
            val entity = userPortfolioExperienceRepository.findById(UUID.fromString(id)).orElseThrow {
                DataNotFoundException("User portfolio experience not found")
            }
            entity.title = title
            entity.company = company
            entity.location = location
            entity.startDate = startDate
            entity.endDate = endDate
            entity.description = description
            entity.technologies = technologies
            entity.highlights = highlights
            val saved = userPortfolioExperienceRepository.save(entity)
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
                data = saved
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    @Transactional
    override fun deletePortfolioProject(id: String): BaseResponse<Any> {
        try {
            val entity = userPortfolioProjectRepository.findById(UUID.fromString(id)).orElseThrow {
                DataNotFoundException("User portfolio project not found")
            }
            userPortfolioProjectRepository.delete(entity)
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    @Transactional
    override fun deletePortfolioSkill(id: String): BaseResponse<Any> {
        try {
            val entity = userPortfolioSkillRepository.findById(UUID.fromString(id)).orElseThrow {
                DataNotFoundException("User portfolio skill not found")
            }
            userPortfolioSkillRepository.delete(entity)
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }

    @Transactional
    override fun deletePortfolioExperience(id: String): BaseResponse<Any> {
        try {
            val entity = userPortfolioExperienceRepository.findById(UUID.fromString(id)).orElseThrow {
                DataNotFoundException("User portfolio experience not found")
            }
            userPortfolioExperienceRepository.delete(entity)
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Update Success"),
            )
        } catch (e: Exception) {
            throw Exception("error")
        }
    }


}