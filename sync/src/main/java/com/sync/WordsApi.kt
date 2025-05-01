package com.sync

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay

class WordsApi(private val client: HttpClient) {

    private val WORDS_API = "https://api.wordnik.com/v4/word.json/"
    private val API_KEY = "n9plmfobgrx43ypim75c1ca06p5rzjz3n3j744coub94bezh2"

    suspend fun getWordsDetail(word: String): Map<String, Any> {

        val definition = getDefinition(word)
        delay(3_000)
        val relatedWords = getRelatedWords(word)
        delay(3_000)
        val audio = getPronunciation(word)
        delay(5_000)
        val examples = getExamples(word)
        val combinedResponse = definition + relatedWords + audio + examples
        println("the combined response is: $combinedResponse")
        return combinedResponse
    }

    suspend fun getDefinition(word: String): Map<String, Any> {
        val response = client.get("${WORDS_API}${word}/definitions?api_key=$API_KEY")
            .body<List<DefinitionResponse>>()
        return mapOf(
            "meaning" to response.map { def ->
                mapOf(
                    "partOfSpeech" to def.partOfSpeech,
                    "definition" to def.text
                )
            },
        )
    }

    suspend fun getRelatedWords(word: String): Map<String, Any> {
        val response = client.get("${WORDS_API}${word}/relatedWords?api_key=$API_KEY")
            .body<List<RelatedWords>>()
        return response.associate { related ->
            related.relationshipType to related.words
        }
    }

    suspend fun getPronunciation(word: String): Map<String, Any> {
        return try {


            val response =
                client.get("${WORDS_API}${word}/audio?api_key=$API_KEY").body<List<AudioResponse>>()

            return mapOf(
                "audio" to response.map { def ->
                    mapOf(
                        "audioUrl" to def.fileUrl,
                        "duration" to def.duration
                    )
                },
            )
        } catch (e: Exception) {
            mapOf(
                "audio" to emptyList<Any>()
            )
        }
    }

    suspend fun getExamples(word: String): Map<String, Any> {
        return try {
            val response = client.get("${WORDS_API}${word}/examples?api_key=$API_KEY")
                .body<SentencesResponse>()
            return mapOf(
                "sentences" to response.sentences.map { def ->
                    def.text
                },
            )
        } catch (e: Exception) {
            mapOf(
                "sentences" to emptyList<Any>()
            )
        }
    }

}