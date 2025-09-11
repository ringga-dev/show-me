package ngga.ring.myblog.data.primary.service

import jakarta.persistence.Column
import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.core.base.BaseResponseList
import ngga.ring.myblog.core.base.MetaData
import ngga.ring.myblog.data.primary.db.entity.BlogEntity
import ngga.ring.myblog.data.primary.repositories.BlogRepository
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

/* =========================================
 * =========== SERVICE INTERFACE ============
 * (sesuai permintaan + tambahan fungsi)
 * ========================================= */
interface BlogService {

    fun findBySlug(slug: String): Any

    fun updateBlog(
        id: String,
        title: String? = null,
        slug: String? = null,
        excerpt: String? = null,
        content: String? = null,
        coverImage: String? = null,
        published: Boolean? = null,
        metaTitle: String? = null,
        metaDescription: String? = null,
        isActive: Boolean? = null,
        categories: List<String>? = null, // CSV "tech,android"
        tags: List<String>? = null,       // CSV "kotlin,spring"
    ): Any

    fun addBlog(
        title: String? = null,
        slug: String? = null,
        excerpt: String? = null,
        content: String? = null,
        coverImage: String? = null,
        published: Boolean? = null,
        metaTitle: String? = null,
        metaDescription: String? = null,
        isActive: Boolean? = null,
        categories: List<String>? = null,
        tags: List<String>? = null,
    ): Any

    fun releseBlog( // mengikuti penamaan kamu; bisa dianggap "publish/unpublish"
        id: String,
        published: Boolean? = null,
    ): Any

    fun deleteBlog(id: String): Any

    /* ===== Tambahan yang sering dibutuhkan ===== */
    fun findById(id: String): Any
    fun list(page: Int = 0, size: Int = 10, q: String? = null, published: Boolean? = null, active: Boolean? = null): Any
    fun hardDelete(id: String): Any
    fun restore(id: String): Any
    fun toggleActive(id: String, active: Boolean): Any
    fun incrementView(slug: String): Any
    fun existsBySlug(slug: String): Boolean
}

/* =========================================
 * =========== SERVICE IMPL =================
 * ========================================= */
@Service
class BlogServiceImpl(
    private val blogRepository: BlogRepository
) : BlogService {

    override fun findBySlug(slug: String): Any {
        val data = blogRepository.findBySlugAndDeletedAtIsNull(slug)
            ?: throw NoSuchElementException("Blog with slug=$slug not found")
        return BaseResponse(
            code = 200,
            status = HttpStatus.OK,
            message = listOf("Blog retrieved successfully"),
            data = data
        )
    }

    override fun findById(id: String): Any {
        val uuid = id.toUUID()
        val data = blogRepository.findByIdAndDeletedAtIsNull(uuid)
            ?: throw NoSuchElementException("Blog with id=$id not found")
        return BaseResponse(
            code = 200,
            status = HttpStatus.OK,
            message = listOf("Blog retrieved successfully"),
            data
        )
    }

    @Transactional
    override fun addBlog(
        title: String?,
        slug: String?,
        excerpt: String?,
        content: String?,
        coverImage: String?,
        published: Boolean?,
        metaTitle: String?,
        metaDescription: String?,
        isActive: Boolean?,
        categories: List<String>?,
        tags: List<String>?,
    ): Any {
        val safeTitle = requireNotNull(title?.takeIf { it.isNotBlank() }) { "title is required" }
        val desiredSlug = if (!slug.isNullOrBlank()) slug else safeTitle
        val finalSlug = uniqueSlug(desiredSlug) { blogRepository.existsBySlug(it) }

        val entity = BlogEntity(
            title = safeTitle,
            slug = finalSlug,
            excerpt = excerpt,
            content = content,
            coverImage = coverImage,
            published = published ?: false,
            metaTitle = metaTitle,
            metaDescription = metaDescription,
            isActive = isActive ?: true,
            categories = categories,
            tags = tags
        )

        val saved = blogRepository.save(entity)
        return BaseResponse(
            code = 200,
            status = HttpStatus.OK,
            message = listOf("Blog added successfully"),
            data = saved
        )
    }

    @Transactional
    override fun updateBlog(
        id: String,
        title: String?,
        slug: String?,
        excerpt: String?,
        content: String?,
        coverImage: String?,
        published: Boolean?,
        metaTitle: String?,
        metaDescription: String?,
        isActive: Boolean?,
        categories: List<String>?,
        tags: List<String>?,
    ): Any {
        val uuid = id.toUUID()
        val entity = blogRepository.findByIdAndDeletedAtIsNull(uuid)
            ?: throw NoSuchElementException("Blog with id=$id not found")

        title?.let { if (it.isBlank()) throw IllegalArgumentException("title cannot be blank") }
        slug?.let {
            val normalized = normalizeSlug(it)
            if (normalized != entity.slug && blogRepository.existsBySlug(normalized)) {
                throw IllegalArgumentException("slug '$normalized' already exists")
            }
            entity.slug = normalized
        }

        if (!title.isNullOrBlank()) entity.title = title
        if (excerpt != null) entity.excerpt = excerpt
        if (content != null) entity.content = content
        if (coverImage != null) entity.coverImage = coverImage
        if (published != null) entity.published = published
        if (metaTitle != null) entity.metaTitle = metaTitle
        if (metaDescription != null) entity.metaDescription = metaDescription
        if (isActive != null) entity.isActive = isActive
        if (categories != null) entity.categories = categories
        if (tags != null) entity.tags = tags

        entity.updatedAt = LocalDateTime.now()

        val saved = blogRepository.save(entity)
        return BaseResponse(
            code = 200,
            status = HttpStatus.OK,
            message = listOf("Blog updated successfully"),
            data = saved
        )
    }

    @Transactional
    override fun releseBlog(id: String, published: Boolean?): Any {
        val uuid = id.toUUID()
        val entity = blogRepository.findByIdAndDeletedAtIsNull(uuid)
            ?: throw NoSuchElementException("Blog with id=$id not found")

        entity.published = published ?: (!entity.published)
        entity.updatedAt = LocalDateTime.now()
        return BaseResponse(
            code = 200,
            status = HttpStatus.OK,
            message = listOf("Blog updated successfully"),
            data = blogRepository.save(entity)
        )
    }

    @Transactional
    override fun deleteBlog(id: String): Any {
        // Soft delete
        val uuid = id.toUUID()
        val entity = blogRepository.findByIdAndDeletedAtIsNull(uuid)
            ?: throw NoSuchElementException("Blog with id=$id not found")

        entity.deletedAt = LocalDateTime.now()
        entity.updatedAt = LocalDateTime.now()
        return BaseResponse(
            code = 200,
            status = HttpStatus.OK,
            message = listOf("Blog deleted successfully"),
            data = blogRepository.save(entity)
        )
    }

    @Transactional
    override fun hardDelete(id: String): Any {
        val uuid = id.toUUID()
        val entity = blogRepository.findById(uuid).getOrNull()
            ?: throw NoSuchElementException("Blog with id=$id not found")
        blogRepository.delete(entity)
        return mapOf("deleted" to true, "id" to id)
    }

    @Transactional
    override fun restore(id: String): Any {
        val uuid = id.toUUID()
        val entity = blogRepository.findById(uuid).getOrNull()
            ?: throw NoSuchElementException("Blog with id=$id not found")
        entity.deletedAt = null
        entity.updatedAt = LocalDateTime.now()
        return BaseResponse(
            code = 200,
            status = HttpStatus.OK,
            message = listOf("Blog restored successfully"),
            data = blogRepository.save(entity)
        )
    }

    @Transactional
    override fun toggleActive(id: String, active: Boolean): Any {
        val uuid = id.toUUID()
        val entity = blogRepository.findByIdAndDeletedAtIsNull(uuid)
            ?: throw NoSuchElementException("Blog with id=$id not found")
        entity.isActive = active
        entity.updatedAt = LocalDateTime.now()
        return BaseResponse(
            code = 200,
            status = HttpStatus.OK,
            message = listOf("Blog updated successfully"),
            data = blogRepository.save(entity)
        )
    }

    @Transactional
    override fun incrementView(slug: String): Any {
        val entity = blogRepository.findBySlugAndDeletedAtIsNull(slug)
            ?: throw NoSuchElementException("Blog with slug=$slug not found")
        entity.viewCount += 1
        entity.updatedAt = LocalDateTime.now()
        return BaseResponse(
            code = 200,
            status = HttpStatus.OK,
            message = listOf("Blog updated successfully"),
            data = blogRepository.save(entity)
        )
    }

    override fun list(
        page: Int,
        size: Int,
        q: String?,
        published: Boolean?,
        active: Boolean?
    ): Any {
        val pageable = PageRequest.of(page, size)
        val data = blogRepository.search(q, published, active, pageable)
        return BaseResponseList(
            code = 200,
            status = HttpStatus.OK,
            message = listOf("Blog retrieved successfully"),
            data = data.content,
            meta = MetaData(
                page = data.number,
                nextPage = data.number + 1,
                perPage = data.size
            )
        )
    }

    override fun existsBySlug(slug: String): Boolean {
        val normalized = normalizeSlug(slug)
        return blogRepository.existsBySlug(normalized)
    }

    /* ===== Helpers ===== */
    private fun String.toUUID(): UUID =
        try { UUID.fromString(this) } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid UUID: $this")
        }

    private fun normalizeSlug(input: String): String =
        input.lowercase()
            .replace(Regex("[^a-z0-9\\s-]"), "") // buang karakter non-alfanumerik (kecuali spasi & -)
            .replace(Regex("\\s+"), "-")         // spasi -> dash
            .replace(Regex("-+"), "-")           // kompres dash beruntun
            .trim('-')
            .ifBlank { UUID.randomUUID().toString() } // fallback supaya gak kosong

    /**
     * Menghasilkan slug unik berbasis [seed]. Jika [exists(slug)] true,
     * akan mencoba -2, -3, dst sampai ketemu yang belum ada.
     */
    private fun uniqueSlug(seed: String, exists: (String) -> Boolean): String {
        val base = normalizeSlug(seed)
        if (!exists(base)) return base
        var i = 2
        var candidate: String
        do {
            candidate = "$base-$i"
            i++
        } while (exists(candidate))
        return candidate
    }
}