package ngga.ring.myblog.core.config.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtFilter: JwtAuthenticationFilter
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .httpBasic { it.disable() }
            .authorizeHttpRequests { auth ->
                /**
                 * izinkan preflight OPTIONS
                 */
                auth.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**", "/auth/**", "/ws/**")
                    .permitAll()

                /**
                 * api untuk get data mengunakan token dari user
                 */

                auth.requestMatchers(
                    "/api/v1/public/**",
                ).permitAll()

                /**
                 * public endpoint
                 */
                auth.requestMatchers(
                    "/",
                    "/api/v1/auth/**", // tambahkan ini
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/api-docs",
                    "/swagger-resources/**",
                    "/webjars/**",
                    "/api-docs/swagger-config",
                    "/api/webhook/**",
                    "/uploads/**",
                    "/api/public/**",
                    "/api/gemini/**",
                    "/api/review/**",
                    "/review-images/**"
                ).permitAll()

                /**
                 * private endpoint
                 */
                auth.requestMatchers(
                    "/api/user/**",
                    "/api/home/**"
                ).authenticated()

                /**
                 * selain di atas harus login
                 */
                auth.anyRequest().authenticated()
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOriginPatterns = listOf(
                "http://localhost:3000",
                "http://192.168.1.13:3000"
            )
            allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            exposedHeaders = listOf("Authorization")
            allowCredentials = true
        }
        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
    }
}
