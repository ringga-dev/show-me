package ngga.ring.myblog.core.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import ngga.ring.myblog.data.primary.models.AdsModel
import ngga.ring.myblog.data.primary.models.FeatureModel

@Converter
class StringToAdsListConverter : AttributeConverter<List<AdsModel>, String> {

    private val mapper = jacksonObjectMapper()
    private val listType = object : TypeReference<List<AdsModel>>() {}

    override fun convertToDatabaseColumn(attribute: List<AdsModel>?): String {
        // Simpan sebagai JSON; gunakan "[]" kalau null/kosong
        return if (attribute.isNullOrEmpty()) "[]"
        else mapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): List<AdsModel> {
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
