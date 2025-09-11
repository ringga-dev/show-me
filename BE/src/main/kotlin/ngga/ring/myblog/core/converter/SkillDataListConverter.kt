package ngga.ring.myblog.core.converter


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import ngga.ring.myblog.data.primary.db.entity.SkillData

@Converter
class SkillDataListConverter : AttributeConverter<List<SkillData>, String> {
    private val mapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: List<SkillData>?): String {
        // simpan sebagai JSON; pastikan tidak null
        return mapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): List<SkillData> {
        if (dbData.isNullOrBlank()) return emptyList()
        return mapper.readValue(dbData)
    }
}
