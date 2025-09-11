package ngga.ring.myblog.presentation.api.user.dto

import ngga.ring.myblog.data.primary.db.entity.SkillData

data class PortfolioSkillDto(
    var title: String? = null,
    var description: String? = null,
    var icon: String? = null,
    var skillData: List<SkillData>?
)
