package ngga.ring.myblog.core.config.multipart

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.unit.DataSize
import jakarta.servlet.MultipartConfigElement
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.servlet.MultipartConfigFactory

@Configuration
class MultipartConfig {
    @Bean
    fun multipartConfigElement(): MultipartConfigElement {
        val factory = MultipartConfigFactory()
        factory.setMaxFileSize(DataSize.ofMegabytes(1024))      // per file
        factory.setMaxRequestSize(DataSize.ofMegabytes(1024))   // total request
        return factory.createMultipartConfig()
    }
}
