package ngga.ring.myblog.data.auth.jwt

import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.util.Date
import javax.crypto.SecretKey

@Configuration
@ConfigurationProperties(prefix = "jwt")
data class JwtConfig(
    var accessSecret: String = "",
    var refreshSecret: String = "",
    var accessExpiration: Long = 0,
    var refreshExpiration: Long = 0
) {
    fun getAccessTokenKey(): SecretKey = Keys.hmacShaKeyFor(accessSecret.toByteArray())
    fun getAccessExpiration(): Date = Date(System.currentTimeMillis() + accessExpiration * 60 * 1000)

    fun getRefreshTokenKey(): SecretKey =Keys.hmacShaKeyFor(refreshSecret.toByteArray())
    fun getRefreshExpiration(): Date = Date(System.currentTimeMillis() + refreshExpiration * 60 * 1000)
}
