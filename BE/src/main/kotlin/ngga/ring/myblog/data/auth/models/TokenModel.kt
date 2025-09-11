package ngga.ring.myblog.data.auth.models

import java.time.LocalDateTime

data class TokenModel (
    var accessToken: String? = null,
    var refreshToken: String? = null,
    var tokenType: String? = null,
    var loginAt: LocalDateTime? = null,
)