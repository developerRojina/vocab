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

        index = requireNotNull(this["index"] as? String) { "index cannot be null or empty" },
    )
}


fun WordResponse.toWordEntity(): WordEntity {
    return WordEntity(
        id = id,
        word = word,
        meaning = meaning.map {
            Meaning(
                it.definition.replace(Regex("<[^>]*>"), ""),
                it.partOfSpeech
            )
        },
        audios = audios?.map { Audio(it.audioUrl, it.duration) } ?: emptyList(),
        antonyms = synonyms ?: emptyList(),
        synonyms = antonyms ?: emptyList(),
        sentences = sentences?.map { it.replace(Regex("<[^>]*>"), "") } ?: emptyList(),
        forms = forms ?: emptyList(),
        etymologicallyRelatedWords = etymologicallyRelatedWords ?: emptyList(),
        hypernyms = hypernyms ?: emptyList(),
        rhymes = rhymes ?: emptyList(),
        contexts = contexts ?: emptyList(),
        equivalents = equivalents ?: emptyList(),
        createdAt = TimeAndDateUtils.getCurrentTimeStampEpocMillis(),
        updatedAt = TimeAndDateUtils.getCurrentTimeStampEpocMillis(),
        wordStatus = WordStatus.Available
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
        audio = audios
    )
}



