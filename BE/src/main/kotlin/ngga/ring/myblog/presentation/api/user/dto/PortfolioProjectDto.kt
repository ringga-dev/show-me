package ngga.ring.myblog.presentation.api.user.dto

import java.time.LocalDateTime

data class PortfolioProjectDto(
    var title: String? = null,
    var imageUrl: String? = null,
    var githubUrl: String? = null,
    var demoUrl: String? = null,
    var description: String? = null,
    var technologies: List<String>? = null,
    var dateCreated: LocalDateTime? = null,
    var userRequest: Int? = null,
    var rating: Int? = null,
    var releseVersion: String? = null
)