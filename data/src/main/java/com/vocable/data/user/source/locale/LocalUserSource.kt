package com.vocable.data.user.source.locale

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.utils.UserKeyUtils
import com.vocable.data.result.AppResult
import com.vocable.data.result.safeCall
import com.vocable.data.user.domain.model.AppUserDetail
import com.vocable.data.user.domain.model.UserPreference
import com.vocable.data.user.domain.model.VocabStats
import com.vocable.data.user.mapper.SelectedTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore by preferencesDataStore(name = "user_data")


class LocalUserSource(val context: Context) {

    private val USER_ID = stringPreferencesKey(UserKeyUtils.KEY_USER_ID)
    private val USER_EMAIL = stringPreferencesKey(UserKeyUtils.KEY_USER_EMAIL)
    private val ACTIVE_WORDS = stringPreferencesKey(UserKeyUtils.KEY_USER_ACTIVE_WORDS)
    private val LEARNED_WORDS = stringPreferencesKey(UserKeyUtils.KEY_USER_LEARNED_WORDS)
    private val QUIZZED_WORDS = stringPreferencesKey(UserKeyUtils.KEY_USER_QUIZZED_WORDS)
    private val LEARNED_WORDS_INDEXES =
        stringPreferencesKey(UserKeyUtils.KEY_USER_LEARNED_WORDS_INDEXES)
    private val DISPLAY_NAME = stringPreferencesKey(UserKeyUtils.KEY_USER_DISPLAY_NAME)
    private val PHONE_NUMBER = stringPreferencesKey(UserKeyUtils.KEY_USER_PHONE_NUMBER)
    private val PROFILE_IMAGE = stringPreferencesKey(UserKeyUtils.KEY_USER_PROFILE_IMAGE)
    private val LOGGED_IN_TIME = stringPreferencesKey(UserKeyUtils.KEY_USER_LOGGED_IN_TIME)
    private val QUIZ_NOTIFICATION_TIME =
        stringPreferencesKey(UserKeyUtils.KEY_SETTINGS_QUIZ_NOTIFICATION_TIME)
    private val NEW_WORDS_NOTIFICATION_TIME =
        longPreferencesKey(UserKeyUtils.KEY_SETTINGS_NEW_WORDS_NOTIFICATION_TIME)
    private val DAILY_WORDS_QUOTA_COUNT =
        intPreferencesKey(UserKeyUtils.KEY_SETTINGS_DAILY_WORDS_QUOTA_COUNT)
    private val WORDS_REMINER_TIME =
        stringPreferencesKey(UserKeyUtils.KEY_SETTINGS_WORDS_REMINDER_NOTIFICATION_TIME)
    private val DISPLAY_THEME = stringPreferencesKey(UserKeyUtils.KEY_SETTINGS_DISPLAY_THEME)
    private val SCORE = intPreferencesKey(UserKeyUtils.KEY_USER_SCORE)

    suspend fun saveUserDetails(appUserDetail: AppUserDetail): AppResult<Unit> = safeCall {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = appUserDetail.id
            preferences[USER_EMAIL] = appUserDetail.email
            preferences[ACTIVE_WORDS] = appUserDetail.vocabStats.currentWords.joinToString(",")
            preferences[LEARNED_WORDS] = appUserDetail.vocabStats.learnedWords.joinToString(",")
            preferences[QUIZZED_WORDS] = appUserDetail.vocabStats.quizzedWords.joinToString(",")
            preferences[QUIZ_NOTIFICATION_TIME] =
                appUserDetail.preference.quizNotificationTimes.joinToString(",")
            preferences[NEW_WORDS_NOTIFICATION_TIME] =
                appUserDetail.preference.newWordsNotificationTime
            preferences[WORDS_REMINER_TIME] =
                appUserDetail.preference.wordsReminder.joinToString(",")
            preferences[QUIZZED_WORDS] = appUserDetail.vocabStats.quizzedWords.joinToString(",")
            preferences[DAILY_WORDS_QUOTA_COUNT] = appUserDetail.preference.dailyWordQuota
            preferences[SCORE] = appUserDetail.score
            preferences[DISPLAY_NAME] = appUserDetail.displayName ?: ""
            preferences[PHONE_NUMBER] = appUserDetail.phoneNumber ?: ""
            preferences[PROFILE_IMAGE] = appUserDetail.profileImage ?: ""
            preferences[DISPLAY_THEME] = appUserDetail.preference.selectedTheme.name

        }
    }

    suspend fun updateNewWordNotification(time: Long): AppResult<Unit> = safeCall {
        context.dataStore.edit { preferences ->
            preferences[NEW_WORDS_NOTIFICATION_TIME] =
                time
        }
    }

    suspend fun updateWordReminderNotification(times: List<Long>): AppResult<Unit> = safeCall {
        context.dataStore.edit { preferences ->
            preferences[WORDS_REMINER_TIME] =
                times.joinToString(",")
        }
    }

    suspend fun updateQuizNotification(times: List<Long>): AppResult<Unit> = safeCall {
        context.dataStore.edit { preferences ->
            preferences[QUIZ_NOTIFICATION_TIME] =
                times.joinToString(",")
        }
    }

    suspend fun updateAppTheme(displayTheme: SelectedTheme): AppResult<Unit> = safeCall {
        context.dataStore.edit { preferences ->
            preferences[DISPLAY_THEME] =
                displayTheme.name
        }
    }

    suspend fun updateWordQuotaCount(quota: Int): AppResult<Unit> = safeCall {
        context.dataStore.edit { preferences ->
            preferences[DAILY_WORDS_QUOTA_COUNT] =
                quota
        }
    }

    suspend fun updateActiveWords(words: List<String>): AppResult<Unit> = safeCall {
        context.dataStore.edit { preferences ->
            preferences[ACTIVE_WORDS] =
                words.joinToString(",")
        }
    }


    suspend fun updateLearnedWords(
        words: List<String>
    ): AppResult<Unit> = safeCall {
        context.dataStore.edit { preferences ->
            val existing = preferences[LEARNED_WORDS]?.split(",")?.toMutableSet() ?: mutableSetOf()
            existing.addAll(words)
            preferences[LEARNED_WORDS] = words.joinToString(",")
        }
    }


    suspend fun updateQuizzedWords(
        words: List<String>
    ): AppResult<Unit> = safeCall {
        context.dataStore.edit { preferences ->
            preferences[QUIZZED_WORDS] = words.joinToString(",")
        }
    }


    fun getUserDetail(): Flow<AppUserDetail?> {
        return context.dataStore.data.map {
            AppUserDetail(
                id = it[USER_ID] ?: "",
                email = it[USER_EMAIL] ?: "",
                vocabStats = VocabStats(
                    currentWords = it[ACTIVE_WORDS]?.split(",") ?: emptyList(),
                    learnedWords = it[LEARNED_WORDS]?.split(",") ?: emptyList(),
                    quizzedWords = it[QUIZZED_WORDS]?.split(",") ?: emptyList(),
                    learnedWordsIndex = it[LEARNED_WORDS_INDEXES]?.split(",")
                        ?.mapNotNull { str -> str.toIntOrNull() } ?: emptyList(),
                ),
                preference = UserPreference(
                    dailyWordQuota = it[DAILY_WORDS_QUOTA_COUNT] ?: 0,
                    quizNotificationTimes = it[QUIZ_NOTIFICATION_TIME]?.split(",")
                        ?.map { it.toLong() }
                        ?: emptyList(),
                    newWordsNotificationTime = it[NEW_WORDS_NOTIFICATION_TIME] ?: 0,
                    wordsReminder = it[WORDS_REMINER_TIME]?.split(",")?.map { it.toLong() }
                        ?: emptyList(),
                    selectedTheme = SelectedTheme.valueOf(
                        it[DISPLAY_THEME] ?: SelectedTheme.DEFAULT.name
                    ),
                ),
                displayName = it[DISPLAY_NAME] ?: "",
                phoneNumber = it[PHONE_NUMBER] ?: "",
                profileImage = it[PROFILE_IMAGE] ?: "",

                score = it[SCORE] ?: 0,
            )
        }

    }

}