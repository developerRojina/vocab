package com.vocable.data.word.domain.model

data class WordResponse(
    val id: String,
    val word: String,
    val meaning: List<MeaningResponse>,
    val index: String,
    val sentences: List<String>?,
    val audios: List<AudioResponse>?,
    val synonyms: List<String>?,
    val hypernyms: List<String>?,
    val contexts: List<String>?,
    val equivalents: List<String>?,
    val forms: List<String>?,
    val etymologicallyRelatedWords: List<String>?,
    val rhymes: List<String>?,
    val antonyms: List<String>?,
)

data class MeaningResponse(
    val definition: String,
    val partOfSpeech: String
)

data class AudioResponse(
    val audioUrl: String,
    val duration: Long
)