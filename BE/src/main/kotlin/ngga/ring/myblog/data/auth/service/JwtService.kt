package ngga.ring.myblog.data.auth.service

import io.jsonwebtoken.*
import ngga.ring.myblog.data.auth.jwt.JwtConfig
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService(
    private val jwtProperties: JwtConfig
) {
    fun generateAccessToken(userId: String): String {
            return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(Date())
                .setExpiration(jwtProperties.getAccessExpiration()) // 15 menit
                .signWith(jwtProperties.getAccessTokenKey(), SignatureAlgorithm.HS256)
                .compact()
    }

    fun generateRefreshToken(userId: String): String {
        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(Date())
            .setExpiration(jwtProperties.getRefreshExpiration()) // 7 hari
            .signWith(jwtProperties.getRefreshTokenKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateAccessToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .setSigningKey(jwtProperties.getAccessTokenKey())
                .build()
                .parseClaimsJws(token)
            true
        } catch (ex: JwtException) {
            println("JWT Access Token Error: ${ex.message}")
            false
        } catch (ex: Exception) {
            println("Unexpected error: ${ex.message}")
            false
        }
    }

    fun validateRefreshToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .setSigningKey(jwtProperties.getRefreshTokenKey())
                .build()
                .parseClaimsJws(token)
            true
        } catch (ex: JwtException) {
            println("JWT Refresh Token Error: ${ex.message}")
            false
        } catch (ex: Exception) {
            println("Unexpected error: ${ex.message}")
            false
        }
    }

    fun getUserIdFromAccessToken(token: String): String {
        val claims = Jwts.parser()
            .setSigningKey(jwtProperties.getAccessTokenKey())
            .build()
            .parseClaimsJws(token.replace("Bearer ", ""))
        return claims.body.subject
    }

    fun getUserIdFromRefreshToken(token: String): String {
        val claims = Jwts.parser()
            .setSigningKey(jwtProperties.getRefreshTokenKey())
            .build()
            .parseClaimsJws(token.replace("Bearer ", ""))
        return claims.body.subject
    }
}
