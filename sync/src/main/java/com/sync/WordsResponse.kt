package com.sync

import kotlinx.serialization.SerialName


@kotlinx.serialization.Serializable
data class RelatedWords(
    val relationshipType: String,
    val words: List<String>
)


@kotlinx.serialization.Serializable
data class AudioResponse(
    val duration: Float?=0f,
    val fileUrl: String?=""
)

@kotlinx.serialization.Serializable
data class SentencesResponse(
    @SerialName("examples")
    val sentences: List<SentenceResponse>,
)

@kotlinx.serialization.Serializable
data class SentenceResponse(
    val text: String?="",
)

@kotlinx.serialization.Serializable
data class DefinitionResponse(
    val partOfSpeech: String = "",
    val text: String = "",
)