package ngga.ring.myblog.core.config.security


import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfig {

    @Value("\${spring.web.cors.allowed-origins}")
    private lateinit var allowedOrigins: String

    @Value("\${spring.web.cors.allowed-methods}")
    private lateinit var allowedMethods: String

    @Bean
    fun corsFilter(): UrlBasedCorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOrigins = this@CorsConfig.allowedOrigins.split(",")
            allowedMethods = this@CorsConfig.allowedMethods.split(",")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)

        return source
    }
}