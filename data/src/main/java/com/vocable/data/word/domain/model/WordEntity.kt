package com.vocable.data.word.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val word: String,
    val meaning: List<Meaning>,
    val audios: List<Audio>,
    val sentences: List<String>,
    val synonyms: List<String>,
    val antonyms: List<String>,
    val hypernyms: List<String>,
    val contexts: List<String>,
    val equivalents: List<String>,
    val forms: List<String>,
    val etymologicallyRelatedWords: List<String>,
    val rhymes: List<String>,
    val createdAt: Long,
    val updatedAt: Long,
    val wordStatus: WordStatus
)

@Serializable
data class Meaning(val meaning: String, val partOfSpeech: String)

@Serializable
data class Audio(val audioUrl: String, val duration: Long)

enum class WordStatus {
    Available, Assigned, Learned,
}