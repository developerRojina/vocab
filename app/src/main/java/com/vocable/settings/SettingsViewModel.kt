package com.vocable.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notification.domain.model.NotificationType
import com.notification.domain.repository.NotificationRepository
import com.vocable.data.result.onError
import com.vocable.data.result.onSuccess
import com.vocable.data.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    val userRepository: UserRepository,
    val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SettingsUiState>(SettingsUiState(isLoading = true))
    val state: StateFlow<SettingsUiState> get() = _state


    init {
        viewModelScope.launch {
            val userDetail = userRepository.getMyDetail()
            userDetail.collect { detail ->
                _state.value = _state.value.copy(userDetail = detail)
            }
        }
    }

    fun showTimePicker(timePickerType: TimePickerType, index: Int) {
        _state.value = _state.value.copy(timePicker = Pair(timePickerType, index))
    }

    fun clearTimePickerType() {
        _state.value = _state.value.copy(timePicker = null)
    }

    fun updateNotificationTime(time: Long) {

        val timePicker = _state.value.timePicker
        timePicker?.let {
            _state.value = _state.value.copy(isLoading = true)
            when (it.first) {
                TimePickerType.WORD_REMINDER_TIME -> {
                    updateWordsReminderNotification(time)
                }

                TimePickerType.NEW_WORDS_TIME -> {
                    updateNewWordsNotification(time)
                }

                TimePickerType.QUIZ_REMINDER_TIME -> {
                    updateQuizReminderNotification(time)
                }
            }

            clearTimePickerType()
        }
    }

    private fun updateNewWordsNotification(time: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            userRepository.updateNewWordNotificationTime(time)
                .onSuccess {
                    _state.value = _state.value.copy(isLoading = false)
                    notificationRepository.updateNotificationTime(time, NotificationType.NEW_WORDS)
                }
                .onError { _state.value = _state.value.copy(isLoading = false) }

        }
    }

    private fun updateWordsReminderNotification(time: Long) {
        viewModelScope.launch {
            _state.value.timePicker?.let { timePicker ->
                val prefs = _state.value.userDetail?.preference
                val times = prefs?.wordsReminder?.toMutableList()
                times?.let {
                    _state.value = _state.value.copy(isLoading = true)
                    times[timePicker.second] = time
                    userRepository.updateWordReminderTime(times)
                        .onSuccess {
                            _state.value = _state.value.copy(isLoading = false)
                            notificationRepository.updateNotificationTime(
                                time,
                                NotificationType.WORD_REMINDER
                            )
                        }
                        .onError { _state.value = _state.value.copy(isLoading = false) }

                }
            }

        }
    }

    private fun updateQuizReminderNotification(time: Long) {
        viewModelScope.launch {
            _state.value.timePicker?.let { timePicker ->
                val prefs = _state.value.userDetail?.preference
                val times = prefs?.wordsReminder?.toMutableList()
                times?.let {
                    _state.value = _state.value.copy(isLoading = true)
                    times[timePicker.second] = time
                    userRepository.updateQuizNotificationsTime(times)
                        .onSuccess {
                            _state.value = _state.value.copy(isLoading = false)
                            notificationRepository.updateNotificationTime(
                                time,
                                NotificationType.QUIZ_REMINDER
                            )
                        }
                        .onError { _state.value = _state.value.copy(isLoading = false) }
                }
            }
        }
    }

    fun updateWordsQuota(quota: Int) {
        viewModelScope.launch {
            userRepository.updateDailyWordQuota(quota)
                .onSuccess { _state.value = _state.value.copy(isLoading = false) }
                .onError { _state.value = _state.value.copy(isLoading = false) }
        }
    }
}