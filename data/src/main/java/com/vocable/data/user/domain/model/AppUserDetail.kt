package com.vocable.data.user.domain.model

import com.vocable.data.user.mapper.SelectedTheme

data class AppUserDetail(
    val id: String,
    val email: String,
    val profileImage: String?,
    val displayName: String?,
    val phoneNumber: String?,
    val score: Int,
    val vocabStats: VocabStats,
    val preference: UserPreference
)


data class VocabStats(
    val learnedWordsIndex: List<Int>,
    val learnedWords: List<String>,
    val quizzedWords: List<String>,
    val currentWords: List<String>,
)

data class UserPreference(
    val quizNotificationTimes: List<Long>,
    val newWordsNotificationTime: Long,
    val wordsReminder: List<Long>,
    val selectedTheme: SelectedTheme,
    val dailyWordQuota: Int,
)