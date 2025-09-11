package ngga.ring.myblog.core.converter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import ngga.ring.myblog.data.primary.db.entity.SocialLink

@Converter(autoApply = false)
class SocialLinkListJsonConverter : AttributeConverter<List<SocialLink>?, String?> {
    private val mapper = jacksonObjectMapper()
    override fun convertToDatabaseColumn(attribute: List<SocialLink>?): String? =
        attribute?.let { mapper.writeValueAsString(it) }

    override fun convertToEntityAttribute(dbData: String?): List<SocialLink>? =
        dbData?.let { mapper.readValue(it) }
}