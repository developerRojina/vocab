package com.vocable.data.user.domain.repository

import com.vocable.data.result.AppResult
import com.vocable.data.user.domain.model.AppUserDetail
import com.vocable.data.user.mapper.SelectedTheme
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun addUserDetail(appUser: AppUserDetail): AppResult<Unit>
    suspend fun saveUserDetail(appUser: AppUserDetail): AppResult<Unit>
    suspend fun getMyDetail(): Flow<AppUserDetail?>

    suspend fun updateQuizNotificationsTime(times: List<Long>): AppResult<Unit>
    suspend fun updateWordReminderTime(times: List<Long>): AppResult<Unit>
    suspend fun updateNewWordNotificationTime(time: Long): AppResult<Unit>
    suspend fun updateDailyWordQuota(quota: Int): AppResult<Unit>
    suspend fun updateAppTheme(selectedTheme: SelectedTheme): AppResult<Unit>

    suspend fun updateCurrentWords(words: List<String>): AppResult<Unit>
    suspend fun updateQuizzedWords(words: List<String>): AppResult<Unit>
    suspend fun updateLearnedWords(words: List<String>): AppResult<Unit>

    suspend fun getUserDetail(id: String): AppUserDetail?


}