package ngga.ring.myblog.data.primary.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.*
import java.util.UUID

@Service
class FileService(
    @Value("\${app.upload-dir}") uploadDirProp: String
) {

    private val uploadDir: Path = Paths.get(uploadDirProp).toAbsolutePath().normalize()

    init {
        Files.createDirectories(uploadDir)
    }

    fun saveFile(file: MultipartFile): String {
        require(!file.isEmpty) { "File kosong" }

        val original = file.originalFilename ?: "file"
        val ext = original.substringAfterLast('.', "").lowercase().let { if (it.isNotBlank()) ".$it" else "" }
        val safeName = "${UUID.randomUUID()}$ext"

        val target = uploadDir.resolve(safeName).normalize()
        require(target.startsWith(uploadDir)) { "Invalid path" }

        file.inputStream.use { input ->
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING)
        }
        return safeName
    }

    fun resolveFile(filename: String): Path {
        require(!filename.contains("..")) { "Invalid filename" }
        val path = uploadDir.resolve(filename).normalize()
        require(path.startsWith(uploadDir)) { "Invalid path" }
        require(Files.exists(path)) { "File tidak ditemukan" }
        return path
    }

    // ===== Tambahan: List & Delete =====

    data class FileMeta(
        val filename: String,
        val size: Long,
        val lastModified: Long,
        val contentType: String?
    )

    fun listFiles(): List<FileMeta> {
        Files.createDirectories(uploadDir)
        Files.list(uploadDir).use { stream ->
            return stream
                .filter { Files.isRegularFile(it) }
                .map { p ->
                    FileMeta(
                        filename = p.fileName.toString(),
                        size = Files.size(p),
                        lastModified = Files.getLastModifiedTime(p).toMillis(),
                        contentType = Files.probeContentType(p)
                    )
                }
                .sorted { a, b -> (b.lastModified - a.lastModified).toInt() } // terbaru dulu
                .toList()
        }
    }

    fun deleteFile(filename: String): Boolean {
        require(!filename.contains("..")) { "Invalid filename" }
        val target = uploadDir.resolve(filename).normalize()
        require(target.startsWith(uploadDir)) { "Invalid path" }
        return Files.deleteIfExists(target) // true jika terhapus, false jika memang tidak ada
    }

    fun deleteAll(): Int {
        var count = 0
        Files.createDirectories(uploadDir)
        Files.list(uploadDir).use { stream ->
            stream.filter { Files.isRegularFile(it) }.forEach { p ->
                if (Files.deleteIfExists(p)) count++
            }
        }
        return count
    }
}
