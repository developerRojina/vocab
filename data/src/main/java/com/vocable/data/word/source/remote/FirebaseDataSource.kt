package com.vocable.data.word.source.remote

import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.utils.UserKeyUtils.KEY_USER_ACTIVE_WORDS
import com.vocable.data.FirestoreCollections
import com.vocable.data.word.domain.model.WordEntityFirebase
import com.vocable.data.word.domain.model.WordResponse
import com.vocable.data.word.mapper.toWord
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class FirebaseDataSource(private val firestore: FirebaseFirestore) {

    suspend fun getWordsDbCount(): Int {
        val snapshot =
            firestore.collection(FirestoreCollections.words).count().get(AggregateSource.SERVER)
                .await()
        return snapshot.count.toInt()
    }


    suspend fun getRandomDocuments(randomIndices: List<String>): List<String> {
        val wordIds = arrayListOf<String>()
        val reference = firestore.collection(FirestoreCollections.words)
            .whereIn("index", randomIndices.toList())
            .get().await()

        reference.documents.map { item ->
            val id = item["id"] as String
            wordIds.add(id)
        }

        return wordIds
    }

    suspend fun downloadWordsFromIndex(indices: List<String>): List<WordResponse> {
        val words = arrayListOf<WordResponse>()
        val reference = firestore.collection(FirestoreCollections.words)
            .whereIn("index", indices.toList())
            .get().await()

        reference.documents.map { response ->
            val data = response.data?.toWord()
            data?.let { data -> words.add(data) }

        }
        return words
    }

    suspend fun downloadWordFromId(id: String): WordResponse? {
        val reference = firestore.collection(FirestoreCollections.words)
            .document(id)
            .get().await()
        return reference.data?.toWord()
    }

    fun generateRandomIndices(max: Int, count: Int): Set<Int> {
        val indices = mutableSetOf<Int>()
        while (indices.size < count && indices.size < max) {
            indices.add(Random.nextInt(max))
        }
        return indices
    }


    suspend fun getMyWords(userId: String): List<WordResponse> {
        return try {
            val words = arrayListOf<WordResponse>()
            val snapshot =
                firestore.collection(FirestoreCollections.users).document(userId).get().await()
            if (snapshot.exists()) {
                val currentWords = snapshot.get(KEY_USER_ACTIVE_WORDS) as List<*>
                currentWords.map {
                    val response =
                        firestore.collection(FirestoreCollections.words).document(it as String)
                            .get().await()
                    response.data?.let { response ->
                        val word = response.toWord()
                        words.add(word)
                    }
                }
            }
            return words

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    fun writeWordsInDb(json: List<WordEntityFirebase>) {
        json.map {
            firestore.collection(FirestoreCollections.words)
                .document(it.id)
                .set(it)
        }
    }

    fun writeWordsIn(json: Map<String, Any>) {

        firestore.collection(FirestoreCollections.words)
            .document(json["id"].toString())
            .set(json)

    }
}