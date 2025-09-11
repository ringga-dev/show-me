package ngga.ring.myblog.data.primary.service

import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.core.config.error.DataNotFoundException
import ngga.ring.myblog.data.primary.db.entity.AppEntity
import ngga.ring.myblog.data.primary.models.AdsModel
import ngga.ring.myblog.data.primary.models.FeatureModel
import ngga.ring.myblog.data.primary.repositories.AppRepository
import org.apache.coyote.BadRequestException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID


interface AppService {
    fun getAllApp(): Any
    fun getAppById(id: String): Any
    fun getAppBySlug(slug: String): Any
    fun addApp(
        name: String? = null,
        slug: String? = null,
        image: String? = null,
        description: String? = null,
        isActive: Boolean? = null,
        features: List<FeatureModel>? = null,
        ads: List<AdsModel>? = null,
    ): Any

    fun updateApp(
        id: String,
        name: String? = null,
        slug: String? = null,
        image: String? = null,
        description: String? = null,
        isActive: Boolean? = null,
        features: List<FeatureModel>? = null,
        ads: List<AdsModel>? = null,
    ): Any

    fun deleteApp(
        id: String
    ): Any
}

@Service
class AppServiceImpl(
    private val appRepository: AppRepository
) : AppService {

    override fun getAllApp(): Any {
        try {
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf(
                    "All Data Apps"
                ),
                data = appRepository.findAll()
            )
        } catch (e: Exception) {
            throw DataNotFoundException(
                message = e.message?.get(0).toString()
            )
        }
    }

    override fun getAppById(id: String): Any {
        try {
            val data = appRepository.findById(UUID.fromString(id)).orElseThrow {
                DataNotFoundException(
                    message = "Data Not Found"
                )
            }

            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf(
                    "All Data Apps"
                ),
                data = data
            )
        } catch (e: Exception) {
            throw DataNotFoundException(
                message = e.message?.get(0).toString()
            )
        }
    }

    override fun getAppBySlug(slug: String): Any {
        try {
            val data = appRepository.findBySlug(slug).orElseThrow {
                DataNotFoundException(
                    message = "Data Not Found"
                )
            }

            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf(
                    "All Data Apps"
                ),
                data = data
            )
        } catch (e: Exception) {
            throw DataNotFoundException(message = e.message?.get(0).toString())
        }
    }

    override fun addApp(
        name: String?,
        slug: String?,
        image: String?,
        description: String?,
        isActive: Boolean?,
        features: List<FeatureModel>?,
        ads: List<AdsModel>?
    ): Any {
        try {
            if (appRepository.existsBySlug(slug ?: "")) {
                throw BadRequestException("Slug is already registered")
            }
            val apps = AppEntity(
                name = name,
                slug = slug,
                image = image,
                description = description,
                isActive = isActive,
                features = features,
                ads = ads,
                createdAt = LocalDateTime.now()
            )
            val data = appRepository.save(apps)

            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("success app added"),
                data = data
            )


        } catch (e: Exception) {
            throw BadRequestException(e.message?.get(0).toString())
        }
    }

    override fun updateApp(
        id: String,
        name: String?,
        slug: String?,
        image: String?,
        description: String?,
        isActive: Boolean?,
        features: List<FeatureModel>?,
        ads: List<AdsModel>?
    ): Any {
        try {
            val app = appRepository.findById(UUID.fromString(id)).orElseThrow {
                throw BadRequestException("app is not Registered")
            }
            val data = appRepository.save(
                app.copy(
                    name = name,
                    slug = slug,
                    image = image,
                    description = description,
                    isActive = isActive,
                    features = features,
                    ads = ads,
                    updatedAt = LocalDateTime.now()
                )
            )
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("success app updated"),
                data = data
            )
        } catch (e: Exception) {
            throw BadRequestException(
                e.message?.get(0).toString()
            )
        }
    }

    override fun deleteApp(id: String): Any {
        try {
            val app = appRepository.findById(UUID.fromString(id)).orElseThrow {
                throw BadRequestException("app not found")
            }
            appRepository.delete(app)

            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("success app deleted"),
                data = null
            )
        } catch (e: Exception) {
            throw BadRequestException(
                e.message?.get(0).toString()
            )
        }
    }


}