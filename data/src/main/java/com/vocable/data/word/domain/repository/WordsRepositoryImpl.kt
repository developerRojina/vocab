package com.vocable.data.word.domain.repository

import android.content.Context
import android.util.Log
import com.sync.WordsApi
import com.vocable.data.word.domain.model.Word
import com.vocable.data.word.domain.model.WordStatus
import com.vocable.data.word.mapper.toWord
import com.vocable.data.word.mapper.toWordEntity
import com.vocable.data.word.source.local.WordsDao
import com.vocable.data.word.source.remote.FirebaseDataSource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import kotlin.math.min

class WordsRepositoryImpl(
    private val context: Context,
    private val firebaseDataSource: FirebaseDataSource,
    private val wordsDao: WordsDao,
    private val wordsApi: WordsApi
) :
    WordsRepository {

    val WORDS_COUNT_IN_DD = 30

    override suspend fun getWordsOfTheDay(wordsCount: Int): Flow<List<String>> {
        return wordsDao.getWordsByStatus(WordStatus.Available)
            .map { words ->
                val wordsCountInDb = words.size
                Log.d("TAG", "the words count in db is $wordsCountInDb")

                if (wordsCountInDb < wordsCount) {
                    downloadWords(wordsCount, emptyList())
                    return@map emptyList<String>()
                }
                val randomIndexes =
                    (0 until wordsCountInDb).shuffled().take(min(wordsCount, wordsCountInDb))
                randomIndexes.map {
                    Log.d("TAG", "the random index in db is $it")
                    words[it].id
                }
            }
    }


    override suspend fun downloadWords(wordsCount: Int, words: List<String>) {
        if (words.isNotEmpty()) {
            words.forEach { id ->
                val word = getWordDetail(id)
                if (word == null) {
                    firebaseDataSource.downloadWordFromId(id)?.toWordEntity()?.let {
                        wordsDao.saveSeedWord(it)
                    }
                }
            }
        }

        val wordsCountInDb = wordsDao.getAvailableWordsCount(WordStatus.Available).first()
        if (wordsCountInDb < wordsCount) {
            val dbcount = firebaseDataSource.getWordsDbCount()
            val indexes = arrayListOf<String>()
            if (dbcount > WORDS_COUNT_IN_DD) {
                val randomIndexes =
                    (0 until dbcount).shuffled().take(WORDS_COUNT_IN_DD).map { "index$it" }
                indexes.addAll(randomIndexes)
            } else {
                indexes.addAll((0 until dbcount).map { "index$it" })
            }

            val words = firebaseDataSource.downloadWordsFromIndex(indexes)
            wordsDao.saveWords(words.map { it.toWordEntity() })
        }
    }
    override suspend fun getWordDetail(id: String): Word? {
        return wordsDao.getMyWordsById(id)?.toWord()
    }

    override suspend fun updateWordStatusToAssigned(words: List<String>) {
        words.map {
            wordsDao.updateWOrdStatus(it, WordStatus.Assigned)
        }
    }

    override suspend fun updateWordStatusToLearned(words: List<String>) {
        words.map {
            wordsDao.updateWOrdStatus(it, WordStatus.Learned)
        }
    }

    override suspend fun writeWordsInDb() {

        /*   val inputStream = context.assets.open("words_db.json")
           val reader = InputStreamReader(inputStream)
           val jsonData = Json.decodeFromString<List<WordEntityFirebase>>(reader.readText())*/

        val words = getWords()

        coroutineScope {
            words.forEachIndexed { index, word ->
                try {
                    var response = wordsApi.getWordsDetail(words[index])
                    val ids = mapOf<String, String>(
                        "id" to UUID.randomUUID().toString(),
                        "index" to "index${index + 999}",
                        "word" to word
                    )
                    val combinedResponse = ids + response

                    firebaseDataSource.writeWordsIn(combinedResponse)
                    delay(20_000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

    }

    fun getWords(): List<String> {
        return listOf(
            "morbid",
            "allegory",

            )


    }

}