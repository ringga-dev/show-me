package ngga.ring.myblog.presentation.api.public.controller

import ngga.ring.myblog.core.base.BaseResponse
import ngga.ring.myblog.core.config.error.DataNotFoundException
import ngga.ring.myblog.data.primary.service.FileService
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

@RestController
@RequestMapping("${Config.V1.PUBLIC}/files")
class FileController(
    private val fileService: FileService
) {

    data class UploadResponse(
        val url: String,
        val filename: String,
        val size: Long,
        val contentType: String?
    )

    data class FileItem(
        val url: String,
        val filename: String,
        val size: Long,
        val contentType: String?,
        val lastModified: Long
    )

    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<Any> {
        val savedName = fileService.saveFile(file)
        val url = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("${Config.V1.PUBLIC}/files/")
            .path(savedName)
            .toUriString()

        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.ACCEPTED.value(),
                status = HttpStatus.ACCEPTED,
                message = arrayListOf("Saved file $savedName"),
                data = UploadResponse(
                    url = url,
                    filename = savedName,
                    size = file.size,
                    contentType = file.contentType
                )
            )
        )
    }

    // ====== GET satu file (sudah ada) ======
    @GetMapping("/{filename}")
    fun getFile(@PathVariable filename: String): ResponseEntity<Resource> {
        val path = fileService.resolveFile(filename)
        val resource = UrlResource(path.toUri())
        val mime = Files.probeContentType(path) ?: MediaType.APPLICATION_OCTET_STREAM_VALUE

        val inlineTypes = setOf(
            "image/png", "image/jpeg", "image/gif", "image/webp",
            "image/svg+xml", "application/pdf"
        )
        val contentDisposition = if (mime in inlineTypes) "inline" else "attachment"

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(mime))
            .header(HttpHeaders.CONTENT_DISPOSITION, "$contentDisposition; filename=\"${resource.filename}\"")
            .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
            .body(resource)
    }

    // ====== GET semua file ======
    @GetMapping
    fun listFiles(): ResponseEntity<Any> {
        val metas = fileService.listFiles()
        val base = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("${Config.V1.PUBLIC}/files/")
            .toUriString()

        val items = metas.map { m ->
            FileItem(
                url = base + m.filename,
                filename = m.filename,
                size = m.size,
                contentType = m.contentType,
                lastModified = m.lastModified
            )
        }
        return ResponseEntity.ok(
            BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("all file"),
                data = items
            )
        )
    }

    // ====== DELETE satu file ======
    @DeleteMapping("/{filename}")
    fun deleteFile(@PathVariable filename: String): ResponseEntity<Any> {
        val deleted = fileService.deleteFile(filename)
        return if (deleted) {
            ResponseEntity.ok(BaseResponse(
                code = HttpStatus.OK.value(),
                status = HttpStatus.OK,
                message = arrayListOf("Deleted file $filename"),
                data = null
            ))
        } else {
            // tidak ada file tsb
            throw DataNotFoundException(
                "File $filename not found"
            )
        }
    }

    // ====== DELETE semua file ======
    @DeleteMapping
    fun deleteAll(): ResponseEntity<Map<String, Any>> {
        val count = fileService.deleteAll()
        return ResponseEntity.ok(mapOf("deletedCount" to count))
    }
}
