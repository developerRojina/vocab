package com.vocable.settings

import com.vocable.data.user.domain.model.AppUserDetail

data class SettingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userDetail: AppUserDetail? = null,
    val timePicker: Pair<TimePickerType, Int>? = null,
)


enum class TimePickerType {
    WORD_REMINDER_TIME,
    NEW_WORDS_TIME,
    QUIZ_REMINDER_TIME

}