package ngga.ring.myblog.data.primary.service

import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.core.config.error.DataNotFoundException
import ngga.ring.myblog.data.primary.db.entity.PromotionEntity
import ngga.ring.myblog.data.primary.db.entity.TypeStatus
import ngga.ring.myblog.data.primary.repositories.PromotionRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.UUID


interface PromosionService {
    fun findAllPromosion(): BaseResponse<Any>
    fun insertPromosion(
        name: String? = null,       // nama aplikasi
        description: String? = null,
        rating: Float? = null,
        type: TypeStatus? = null,
        url: String? = null,
        pos: Int? = null,
    ): BaseResponse<Any>

    fun updatePromosion(
        id: String,
        name: String? = null,       // nama aplikasi
        description: String? = null,
        rating: Float? = null,
        type: TypeStatus? = null,
        url: String? = null,
        pos: Int? = null,
    ): BaseResponse<Any>

    fun deletePromosion(id: String): BaseResponse<Any>

}

@Service
class PromosionServiceImpl(val repository: PromotionRepository) : PromosionService {
    override fun findAllPromosion(): BaseResponse<Any> {
        try {
            val data = repository.findAll()

            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = listOf("Success"),
                data = data
            )
        } catch (e: Exception) {
            throw DataNotFoundException("Could not find promosion ${e.message}")
        }
    }
    fun test(
        id: String,
    ) {
        print("test")
    }


    override fun insertPromosion(
        name: String?,
        description: String?,
        rating: Float?,
        type: TypeStatus?,
        url: String?,
        pos: Int?
    ): BaseResponse<Any> {
        try {
            val data = PromotionEntity(
                name = name,
                description = description,
                rating = rating,
                type = type,
                url = url,
                pos = pos
            )

            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Success"),
                data = repository.save(data),
            )
        } catch (e: Exception) {
            throw DataNotFoundException("Could not insert promosion")
        }
    }

    override fun updatePromosion(
        id: String,
        name: String?,
        description: String?,
        rating: Float?,
        type: TypeStatus?,
        url: String?,
        pos: Int?
    ): BaseResponse<Any> {
        try {
            val data = PromotionEntity(
                id = UUID.fromString(id),
                name = name,
                description = description,
                rating = rating,
                type = type,
                url = url,
                pos = pos
            )
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Success"),
                data = data,
            )
        } catch (e: Exception) {
            throw DataNotFoundException("Could not update promosion")
        }
    }

    override fun deletePromosion(id: String): BaseResponse<Any> {
        try {
            repository.deleteById(UUID.fromString(id))
            return BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Delete Success"),
            )
        } catch (e: Exception) {
            throw DataNotFoundException("Could not delete promosion")
        }
    }

}