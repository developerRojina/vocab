package com.vocable.data.user.domain.repository

import com.vocable.data.result.AppResult
import com.vocable.data.result.safeCall
import com.vocable.data.user.domain.model.AppUserDetail
import com.vocable.data.user.mapper.SelectedTheme
import com.vocable.data.user.source.locale.LocalUserSource
import com.vocable.data.user.source.remote.FirebaseUserSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class UserRepositoryImpl(
    val firebaseUserSource: FirebaseUserSource,
    val localUserSource: LocalUserSource
) : UserRepository {


    override suspend fun addUserDetail(userDetail: AppUserDetail): AppResult<Unit> {

        return safeCall(
            remote = { firebaseUserSource.addUserInDb(userDetail) },
            local = { saveUserDetail(appUser = userDetail) }
        )
    }

    override suspend fun saveUserDetail(appUser: AppUserDetail): AppResult<Unit> =
        localUserSource.saveUserDetails(appUser)


    override suspend fun getMyDetail(): Flow<AppUserDetail?> {
        return localUserSource.getUserDetail()
    }

    override suspend fun updateQuizNotificationsTime(times: List<Long>): AppResult<Unit> =
        withUserDetail { detail ->
            safeCall(
                remote = { firebaseUserSource.updateNewQuizNotificationTime(detail.id, times) },
                local = { localUserSource.updateQuizNotification(times) }
            )
        }

    override suspend fun updateWordReminderTime(times: List<Long>): AppResult<Unit> =
        withUserDetail { detail ->
            safeCall(
                remote = { firebaseUserSource.updateWordReminderTimes(detail.id, times) },
                local = { localUserSource.updateWordReminderNotification(times) }
            )
        }

    override suspend fun updateNewWordNotificationTime(time: Long): AppResult<Unit> =
        withUserDetail { detail ->
            safeCall(
                remote = { firebaseUserSource.updateNewWordsNotificationTime(detail.id, time) },
                local = { localUserSource.updateNewWordNotification(time) }
            )
        }

    override suspend fun updateDailyWordQuota(quota: Int): AppResult<Unit> =
        withUserDetail { detail ->
            safeCall(
                remote = { firebaseUserSource.updateDailyQuotaNotificationCount(detail.id, quota) },
                local = { localUserSource.updateWordQuotaCount(quota) }
            )
        }

    override suspend fun updateAppTheme(selectedTheme: SelectedTheme): AppResult<Unit> =
        withUserDetail { detail ->
            safeCall(
                remote = { firebaseUserSource.updateAppTheme(detail.id, selectedTheme) },
                local = { localUserSource.updateAppTheme(selectedTheme) }
            )
        }

    override suspend fun updateCurrentWords(words: List<String>): AppResult<Unit> =
        withUserDetail { detail ->
            safeCall(
                remote = { firebaseUserSource.updateActiveWords(detail.id, words) },
                local = { localUserSource.updateActiveWords(words) }
            )
        }

    override suspend fun updateQuizzedWords(words: List<String>): AppResult<Unit> =
        withUserDetail { detail ->
            safeCall(
                remote = { firebaseUserSource.updateQuizzedWords(detail.id, words) },
                local = { localUserSource.updateQuizzedWords(words) }
            )
        }

    override suspend fun updateLearnedWords(words: List<String>): AppResult<Unit> =
        withUserDetail { detail ->
            safeCall(
                remote = { firebaseUserSource.updateLearnedWords(detail.id, words) },
                local = { localUserSource.updateLearnedWords(words) }
            )
        }

    override suspend fun getUserDetail(id: String): AppUserDetail? {
        return firebaseUserSource.getUserDetail(id)
    }

    private suspend fun withUserDetail(
        block: suspend (AppUserDetail) -> AppResult<Unit>
    ): AppResult<Unit> {
        val detail = getMyDetail().firstOrNull()
        return if (detail == null) {
            AppResult.Error(Exception("User null"))
        } else {
            block(detail)
        }
    }
}