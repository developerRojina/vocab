package com.vocable.data.word.domain.model

data class Word(
    val id: String,
    val word: String,
    val status: WordStatus,
    val meaning: List<Meaning>,
    val audio: List<Audio>?,
    val sentences: List<String>?,
    val synonyms: List<String>?,
    val antonyms: List<String>?,
    val hypernyms: List<String>?,
    val contexts: List<String>?,
    val equivalents: List<String>?,
    val forms: List<String>?,
    val etymologicallyRelatedWords: List<String>?,
    val rhymes: List<String>?,
)

