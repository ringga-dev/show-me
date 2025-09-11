package ngga.ring.myblog.presentation.api.user.dto

import kotlin.String

data class PortfolioExperienceDto(
    var title: String? = null,
    var company: String? = null,
    var location: String? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    var description: String? = null,
    var technologies: List<String>? = null,
    var highlights: List<String>? = null
)
