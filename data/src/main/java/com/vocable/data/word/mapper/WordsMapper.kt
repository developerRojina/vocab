package com.vocable.data.word.mapper

import com.utils.TimeAndDateUtils
import com.vocable.data.word.domain.model.Audio
import com.vocable.data.word.domain.model.AudioResponse
import com.vocable.data.word.domain.model.Meaning
import com.vocable.data.word.domain.model.MeaningResponse
import com.vocable.data.word.domain.model.Word
import com.vocable.data.word.domain.model.WordEntity
import com.vocable.data.word.domain.model.WordResponse
import com.vocable.data.word.domain.model.WordStatus


fun Map<String, Any>.toWord(): WordResponse {
    return WordResponse(
        id = requireNotNull(this["id"] as? String) { "id cannot be null or empty" },
        word = requireNotNull(this["word"] as? String) { "word cannot be null or empty" },
        meaning = (this["meaning"] as? List<Map<String, Any>>)
            ?.mapNotNull { map ->
                try {
                    MeaningResponse(
                        partOfSpeech = map["partOfSpeech"] as? String ?: "",
                        definition = (map["definition"] as? String) ?: ""
                    )
                } catch (_: Exception) {
                    null
                }
            } ?: emptyList(),
        antonyms = (this["antonyms"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        synonyms = (this["synonyms"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        sentences = (this["sentences"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        audios = (this["audio"] as? List<Map<String, Any>>)
            ?.mapNotNull { map ->
                try {
                    AudioResponse(
                        audioUrl = map["url"] as? String ?: return@mapNotNull null,
                        duration = (map["duration"] as? Number)?.toLong() ?: 0
                    )
                } catch (_: Exception) {
                    null
                }
            } ?: emptyList(),
        equivalents = (this["equivalent"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        rhymes = (this["rhyme"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        contexts = (this["same-context"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        hypernyms = (this["hypernym"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        forms = (this["verb-form"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
        etymologicallyRelatedWords = (this["etymologically-related-words"] as? List<*>)?.mapNotNull { it as? String }
            ?: emptyList(),

        index = this["index"] as? String ?: ""
    )
}


fun WordResponse.toWordEntity(): WordEntity {
    return WordEntity(
        id = id,
        word = word,
        meaning = meaning
            .map {
                val cleanDefinition = it.definition.replace(Regex("<[^>]*>"), "")
                Meaning(cleanDefinition, it.partOfSpeech)
            }
            .filter { it.meaning.isNotBlank() }
            .take(20),
        audios = audios?.map { Audio(it.audioUrl, it.duration) } ?: emptyList(),
        antonyms = synonyms?.map {
            it.replace(Regex("<[^>]*>"), "")
        }?.filter { it.isNotBlank() }
            ?.take(10) ?: emptyList(),
        synonyms = antonyms?.map {
            it.replace(Regex("<[^>]*>"), "")
        }?.filter { it.isNotBlank() }
            ?.take(10) ?: emptyList(),
        sentences = sentences?.map {
            it.replace(Regex("<[^>]*>"), "")
        }?.filter { it.isNotBlank() }
            ?.take(10) ?: emptyList(),
        forms = forms?.map {
            it.replace(Regex("<[^>]*>"), "")
        }?.filter { it.isNotBlank() }
            ?.take(10) ?: emptyList(),
        etymologicallyRelatedWords = etymologicallyRelatedWords ?: emptyList(),
        hypernyms = hypernyms?.map {
            it.replace(Regex("<[^>]*>"), "")
        }?.filter { it.isNotBlank() }
            ?.take(10) ?: emptyList(),
        rhymes = rhymes?.map {
            it.replace(Regex("<[^>]*>"), "")
        }?.filter { it.isNotBlank() }
            ?.take(10) ?: emptyList(),
        contexts = contexts?.map {
            it.replace(Regex("<[^>]*>"), "")
        }?.filter { it.isNotBlank() }
            ?.take(10) ?: emptyList(),
        equivalents = equivalents?.map {
            it.replace(Regex("<[^>]*>"), "")
        }?.filter { it.isNotBlank() }
            ?.take(10) ?: emptyList(),
        createdAt = TimeAndDateUtils.getCurrentTimeStampEpocMillis(),
        updatedAt = TimeAndDateUtils.getCurrentTimeStampEpocMillis(),
        wordStatus = WordStatus.Available,
        index = index
    )
}

fun WordEntity.toWord(): Word {
    return Word(
        id = id,
        word = word,
        meaning = meaning,
        antonyms = synonyms,
        synonyms = antonyms,
        sentences = sentences,
        status = wordStatus,
        forms = forms,
        etymologicallyRelatedWords = etymologicallyRelatedWords,
        hypernyms = hypernyms,
        rhymes = rhymes,
        contexts = contexts,
        equivalents = equivalents,
        audio = audios,
        index = index
    )
}





