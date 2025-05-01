package com.vocable.data.db

import androidx.room.TypeConverter
import com.vocable.data.word.domain.model.Audio
import com.vocable.data.word.domain.model.Meaning
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DbTypeConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return Json.encodeToString(list ?: emptyList()) // Convert List to JSON String
    }

    @TypeConverter
    fun toList(json: String?): List<String> {
        return json?.let { Json.decodeFromString(it) } ?: emptyList()
    }

    @TypeConverter
    fun fromMeaningList(value: List<Meaning>): String = json.encodeToString(value)

    @TypeConverter
    fun toMeaningList(value: String): List<Meaning> = json.decodeFromString(value)

    @TypeConverter
    fun fromAudioList(value: List<Audio>): String = json.encodeToString(value)

    @TypeConverter
    fun toAudioList(value: String): List<Audio> = json.decodeFromString(value)


}