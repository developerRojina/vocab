package com.vocable.data.word.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vocable.data.word.domain.model.WordEntity
import com.vocable.data.word.domain.model.WordStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface WordsDao {


    @Query("Select * from words WHERE wordStatus =:status")
    fun getWordsByStatus(status: WordStatus): Flow<List<WordEntity>>

    @Query("Select COUNT(*) from words WHERE wordStatus =:status")
    fun getAvailableWordsCount(status: WordStatus): Flow<Int>

    @Query("SELECT * FROM words WHERE id = :id")
    suspend fun getMyWordsById(id: String): WordEntity?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWords(words: List<WordEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSeedWord(words: WordEntity)


    @Query("UPDATE words SET wordStatus = :status WHERE id = :id")
    suspend fun updateWOrdStatus(id: String, status: WordStatus)


}