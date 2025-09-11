package ngga.ring.myblog.core.config.security


import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import ngga.ring.myblog.core.config.error.UnauthorizedException
import ngga.ring.myblog.data.auth.service.JwtService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val mapper: ObjectMapper
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)

        try {
            if (!jwtService.validateAccessToken(token)) {
                writeUnauthorized(response, "Token invalid")
                return
            }

            val userId = jwtService.getUserIdFromAccessToken(token)

            val authentication = UsernamePasswordAuthenticationToken(
                userId,
                null,
                emptyList()
            )
            SecurityContextHolder.getContext().authentication = authentication

        } catch (ex: ExpiredJwtException) {
            writeUnauthorized(response, "Token expired")
            return
        } catch (ex: JwtException) {
            writeUnauthorized(response, "Malformed token")
            return
        }

        filterChain.doFilter(request, response)
    }

    private fun writeUnauthorized(res: HttpServletResponse, msg: String) {
        res.status = HttpServletResponse.SC_UNAUTHORIZED
        res.contentType = "application/json"
        val body = mapOf(
            "status" to 401,
            "error" to "Unauthorized",
            "message" to msg
        )
        mapper.writeValue(res.writer, body)

    }
}