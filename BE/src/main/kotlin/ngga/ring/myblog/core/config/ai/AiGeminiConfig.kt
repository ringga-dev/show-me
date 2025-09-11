package ngga.ring.myblog.core.config.ai

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "ngga.ring")
data class AiGeminiConfig(
    var geminiModel: String = "",
    var geminiToken: String = "",
    var gptModel: String="",
    var gptToken: String=""
)