package ngga.ring.myblog.core.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import ngga.ring.myblog.data.primary.models.FeatureModel

@Converter
class StringToFeatureListConverter : AttributeConverter<List<FeatureModel>, String> {

    private val mapper = jacksonObjectMapper()
    private val listType = object : TypeReference<List<FeatureModel>>() {}

    override fun convertToDatabaseColumn(attribute: List<FeatureModel>?): String {
        // Simpan sebagai JSON; gunakan "[]" kalau null/kosong
        return if (attribute.isNullOrEmpty()) "[]"
        else mapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): List<FeatureModel> {
        // Kembalikan list kosong kalau kolom null/blank
        if (dbData.isNullOrBlank()) return emptyList()
        return try {
            mapper.readValue(dbData, listType)
        } catch (e: Exception) {
            // Kalau format di DB tidak valid, fail-safe: kembalikan list kosong
            emptyList()
        }
    }
}
