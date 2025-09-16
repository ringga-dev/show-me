package ngga.ring.myblog.data.primary.service

import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.data.primary.db.entity.TokenAppsEntity
import ngga.ring.myblog.data.primary.repositories.TokenAppsRepository
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID


interface TokenAppsService {
    fun create(
        name: String,
        token: String,
        quota: Int,
        usageCount: Int,
        isActive: Boolean = true,
        expiredAt: LocalDateTime? = null,
        note: String? = null
    ): Any

    fun getById(id: String): Any
    fun getAll(): Any

    fun update(
        id: String,
        name: String,
        token: String,
        quota: Int,
        usageCount: Int,
        isActive: Boolean = true,
        expiredAt: LocalDateTime? = null,
        note: String? = null
    ): Any

    fun delete(id: String): Any

    fun validateToken(token: String): Boolean

    fun incrementUsage(token: String)

}


@Service
class TokenAppsServiceImpl(
    private val tokenAppsRepository: TokenAppsRepository
) : TokenAppsService {

    override fun create(
        name: String,
        token: String,
        quota: Int,
        usageCount: Int,
        isActive: Boolean,
        expiredAt: LocalDateTime?,
        note: String?
    ): Any {
        val data=  tokenAppsRepository.save(
            TokenAppsEntity(
                name = name,
                token = token,
                quota = quota,
                usageCount = usageCount,
                isActive = isActive,
                expiredAt = expiredAt ?: LocalDateTime.now(),
                note = note
            )
        )
        return BaseResponse(
            code = HttpStatus.OK.value(),
            status = HttpStatus.OK,
            message = arrayListOf("Data Succes addet"),
            data = data
        )
    }

    override fun getById(id: String): Any {
        return tokenAppsRepository.findById(UUID.fromString(id)).map {
            BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Data berhasil ditemukan"),
                data = it
            )
        }.orElseThrow {
            throw Exception("Data tidak ditemukan")
        }
    }

    override fun getAll(): Any {
        val data = tokenAppsRepository.findAll()
        return BaseResponse(
            code = HttpStatus.OK.value(),
            status = HttpStatus.OK,
            message = arrayListOf("Data berhasil ditemukan"),
            data = data
        )
    }



    override fun update(
        id: String,
        name: String,
        token: String,
        quota: Int,
        usageCount: Int,
        isActive: Boolean,
        expiredAt: LocalDateTime?,
        note: String?
    ): Any {
        val data = tokenAppsRepository.findById(UUID.fromString(id)).map { existing ->
            val toSave = existing.copy(
                name = name,
                token = token,
                quota = quota,
                usageCount = usageCount,
                isActive = isActive,
                expiredAt = expiredAt ?: LocalDateTime.now(),
                note = note
            )
            tokenAppsRepository.save(toSave)
        }.orElse(null)
        return BaseResponse(
            code = HttpStatus.OK.value(),
            status = HttpStatus.OK,
            message = arrayListOf("Data berhasil diupdate"),
            data = data
        )
    }

    override fun delete(id: String): Any {
        return if (tokenAppsRepository.existsById(UUID.fromString(id))) {
            tokenAppsRepository.deleteById(UUID.fromString(id))
            BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Data berhasil dihapus"),
                data = null
            )
        } else {
            throw Exception("Data tidak ditemukan")
        }
    }

    override fun validateToken(token: String): Boolean {
        val apiToken = tokenAppsRepository.findByToken(token)
        return apiToken.map { isValid(it) }.orElse(false)
    }

    override fun incrementUsage(token: String) {
        tokenAppsRepository.findByToken(token).ifPresent { apiToken ->
            tokenAppsRepository.save(apiToken.copy(
                usageCount = (apiToken.usageCount ?: 0) + 1,
                updatedAt = LocalDateTime.now()
            ))
        }
    }


    private fun isValid(token: TokenAppsEntity): Boolean {
        val now = LocalDateTime.now()
        return token.isActive &&
                (token.expiredAt.isAfter(now)) &&
                (token.usageCount ?: 0) < (token.quota ?: 0)
    }
}