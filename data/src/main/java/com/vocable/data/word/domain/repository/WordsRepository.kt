package com.vocable.data.word.domain.repository

import com.vocable.data.word.domain.model.Word
import com.vocable.data.word.domain.model.WordStatus
import kotlinx.coroutines.flow.Flow

interface WordsRepository {

    suspend fun getWordsOfTheDay(wordsCount: Int): Flow<List<String>>

    suspend fun downloadWords(count: Int, learnedWordsIndex: List<String>)
    suspend fun downloadExistingWords(wordsStatus: WordStatus, words: List<String>)
    suspend fun getWordDetail(id: String): Word?
    suspend fun updateWordStatusToAssigned(words: List<String>)
    suspend fun updateWordStatusToAvailable(words: String)
    suspend fun updateWordStatusToLearned(wordIds: List<String>)

    //for admin related stuff
    suspend fun writeWordsInDb()


}