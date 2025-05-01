package com.vocable.data.word.domain.repository

import com.vocable.data.word.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface WordsRepository {

    suspend fun getWordsOfTheDay(wordsCount: Int): Flow<List<String>>

    suspend fun downloadWords(count: Int,words: List<String>)
    suspend fun getWordDetail(id: String): Word?
    suspend fun updateWordStatusToAssigned(words: List<String>)
    suspend fun updateWordStatusToLearned(words: List<String>)

    //for admin related stuff
    suspend fun writeWordsInDb()


}