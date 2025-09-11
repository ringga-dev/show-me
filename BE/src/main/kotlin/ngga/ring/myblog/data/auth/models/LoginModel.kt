package ngga.ring.myblog.data.auth.models

data class LoginModel(
    var userName: String? = null,
    var email: String? = null,
    var role: List<String>? = null,
    var status: String? = null,
    var isVerified: Boolean? = false,
    val token: TokenModel? = null
)