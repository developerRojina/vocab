package com.vocable.settings

import com.vocable.data.user.domain.model.AppUserDetail

data class SettingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userDetail: AppUserDetail? = null,
    val timePicker: Pair<TimePickerType, Int>? = null,
    var dialogType: DialogType? = null
)


enum class TimePickerType {
    WORD_REMINDER_TIME,
    NEW_WORDS_TIME,
    QUIZ_REMINDER_TIME
}

enum class DialogType {
    NEW_WORD_INFO,
    WORD_REMINDER_INFO,
    WORD_COUNT_INFO,
    WORD_COUNT_MORE_ERROR,
    WORD_COUNT_LESS_ERROR
}