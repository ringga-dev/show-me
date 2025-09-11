package ngga.ring.myblog.presentation.api.user.dto

import ngga.ring.myblog.data.primary.db.entity.SocialLink
import ngga.ring.myblog.data.primary.db.entity.TypeStatus

data class PortfolioDto(
    var name: String? = null,
    var image: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var titles: List<String>? = null,
    var description: String? = null,
    var hashTag: List<String>? = null,
    var socialMedia: List<SocialLink>? = null,
    var workedWith: List<String>? = null,
    var aboutMe: String? = null
)