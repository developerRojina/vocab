package com.vocable.data.word.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WordEntityFirebase(
    val meaning: String,
    val synonyms: List<String>,
    val antonyms: List<String>,
    val sentences: List<String>,
    val alternativeMeaning: List<String>,
    val pronunciation: String,
    val word: String,
    val index: String,
    val id: String,
)