package com.vocable.data.user.mapper

import com.utils.TimeAndDateUtils
import com.utils.UserKeyUtils.KEY_SETTINGS_DAILY_WORDS_QUOTA_COUNT
import com.utils.UserKeyUtils.KEY_SETTINGS_DISPLAY_THEME
import com.utils.UserKeyUtils.KEY_SETTINGS_NEW_WORDS_NOTIFICATION_TIME
import com.utils.UserKeyUtils.KEY_SETTINGS_QUIZ_NOTIFICATION_TIME
import com.utils.UserKeyUtils.KEY_SETTINGS_WORDS_REMINDER_NOTIFICATION_TIME
import com.utils.UserKeyUtils.KEY_USER_ACTIVE_WORDS
import com.utils.UserKeyUtils.KEY_USER_DISPLAY_NAME
import com.utils.UserKeyUtils.KEY_USER_EMAIL
import com.utils.UserKeyUtils.KEY_USER_ID
import com.utils.UserKeyUtils.KEY_USER_LEARNED_WORDS
import com.utils.UserKeyUtils.KEY_USER_LEARNED_WORDS_INDEXES
import com.utils.UserKeyUtils.KEY_USER_LOGGED_IN_TIME
import com.utils.UserKeyUtils.KEY_USER_PHONE_NUMBER
import com.utils.UserKeyUtils.KEY_USER_PROFILE_IMAGE
import com.utils.UserKeyUtils.KEY_USER_QUIZZED_WORDS
import com.utils.UserKeyUtils.KEY_USER_SCORE
import com.vocable.data.user.domain.model.AppUser
import com.vocable.data.user.domain.model.AppUserDetail
import com.vocable.data.user.domain.model.UserPreference
import com.vocable.data.user.domain.model.VocabStats


fun AppUser.toDefaultUserDetail() = AppUserDetail(
    id = id,
    email = email,
    profileImage = profileImage,
    displayName = displayName,
    phoneNumber = phoneNumber,
    vocabStats = VocabStats(
        currentWords = emptyList(),
        learnedWords = emptyList(),
        quizzedWords = emptyList(),
        learnedWordsIndex = emptyList()
    ),
    preference = UserPreference(
        dailyWordQuota = 3,
        selectedTheme = SelectedTheme.DEFAULT,
        quizNotificationTimes = listOf(
            TimeAndDateUtils.getDefaultQuizTime(),
        ),
        wordsReminder =
            TimeAndDateUtils.getDefaultWordsReminderTime(),
        newWordsNotificationTime = TimeAndDateUtils.getDefaultNewWordsNotificationTime(),
    ),
    score = 0,
)

fun Map<String, Any?>.toUserDetail() = AppUserDetail(
    id = this[KEY_USER_ID] as String,
    email = this[KEY_USER_EMAIL] as String,
    profileImage = this[KEY_USER_PROFILE_IMAGE] as? String,
    displayName = this[KEY_USER_DISPLAY_NAME] as? String,
    phoneNumber = this[KEY_USER_PHONE_NUMBER] as? String,
    vocabStats = VocabStats(
        currentWords = this[KEY_USER_ACTIVE_WORDS] as? List<String> ?: emptyList(),
        learnedWords = this[KEY_USER_LEARNED_WORDS] as? List<String> ?: emptyList(),
        quizzedWords = this[KEY_USER_QUIZZED_WORDS] as? List<String> ?: emptyList(),
        learnedWordsIndex = this[KEY_USER_LEARNED_WORDS_INDEXES] as? List<Int> ?: emptyList(),
    ),
    preference = UserPreference(
        selectedTheme = SelectedTheme.valueOf(
            this[KEY_SETTINGS_DISPLAY_THEME] as? String ?: SelectedTheme.DEFAULT.name
        ),
        dailyWordQuota = ((this[KEY_SETTINGS_DAILY_WORDS_QUOTA_COUNT] as Long).toInt()),
        quizNotificationTimes = this[KEY_SETTINGS_QUIZ_NOTIFICATION_TIME] as? List<Long>
            ?: emptyList(),
        newWordsNotificationTime = this[KEY_SETTINGS_NEW_WORDS_NOTIFICATION_TIME] as? Long ?: 0L,
        wordsReminder = this[KEY_SETTINGS_WORDS_REMINDER_NOTIFICATION_TIME] as? List<Long>
            ?: emptyList(),
    ),
    score = (this[KEY_USER_SCORE] as Long).toInt(),

    )


fun AppUserDetail.toFirebaseRemoteData(): Map<String, Any?> {
    return mapOf(
        KEY_USER_ID to id,
        KEY_USER_DISPLAY_NAME to displayName,
        KEY_USER_PHONE_NUMBER to phoneNumber,
        KEY_USER_EMAIL to email,
        KEY_USER_PROFILE_IMAGE to profileImage,
        KEY_USER_LOGGED_IN_TIME to phoneNumber,

        KEY_USER_ACTIVE_WORDS to vocabStats.currentWords,
        KEY_USER_QUIZZED_WORDS to vocabStats.quizzedWords,
        KEY_USER_LEARNED_WORDS to vocabStats.learnedWords,

        KEY_SETTINGS_WORDS_REMINDER_NOTIFICATION_TIME to preference.wordsReminder,

        KEY_SETTINGS_QUIZ_NOTIFICATION_TIME to preference.quizNotificationTimes,
        KEY_SETTINGS_NEW_WORDS_NOTIFICATION_TIME to preference.newWordsNotificationTime,
        KEY_SETTINGS_DAILY_WORDS_QUOTA_COUNT to preference.dailyWordQuota,
        KEY_SETTINGS_DISPLAY_THEME to preference.selectedTheme,


        KEY_USER_SCORE to score,

        )

}

enum class SelectedTheme { DEFAULT, DARK, LIGHT }
