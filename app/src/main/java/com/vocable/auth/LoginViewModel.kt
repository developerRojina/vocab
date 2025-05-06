package com.vocable.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notification.domain.model.NotificationType
import com.notification.domain.repository.NotificationRepository
import com.vocable.data.auth.domain.repository.AuthRepository
import com.vocable.data.auth.source.remote.AuthProvider
import com.vocable.data.result.onSuccess
import com.vocable.data.user.domain.model.AppUserDetail
import com.vocable.data.user.domain.model.UserPreference
import com.vocable.data.user.domain.repository.UserRepository
import com.vocable.data.user.mapper.toDefaultUserDetail
import com.vocable.data.word.domain.repository.WordsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val wordsRepository: WordsRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val loginState: StateFlow<LoginUiState> get() = _loginState


    init {
        viewModelScope.launch {
             //  wordsRepository.writeWordsInDb()
        }
    }

    fun login(authProvider: AuthProvider) {
        if (loginState.value == LoginUiState.LoggedIn) return
        viewModelScope.launch {
            val appUser = authRepository.login(authProvider)
            appUser?.let { user ->
                val userDetail: AppUserDetail? = if (user.isNewUser == true) {
                    user.toDefaultUserDetail()
                } else {
                    userRepository.getUserDetail(user.id)
                }

                userDetail?.let { detail ->
                    if (detail.vocabStats.currentWords.isEmpty()) {
                        wordsRepository.downloadWords(
                            userDetail.preference.dailyWordQuota,
                            emptyList()
                        )
                        val words =
                            wordsRepository.getWordsOfTheDay(userDetail.preference.dailyWordQuota)
                                .first()

                        if (words.isNotEmpty()) {
                            Timber.d("the words are: $words")
                            Timber.d("the detail is $detail")
                            userRepository.addUserDetail(
                                detail.copy(vocabStats = detail.vocabStats.copy(currentWords = words))
                            )
                            scheduleAllNotifications(preference = userDetail.preference)
                            successfulLogin(words)
                        }
                    } else {
                        wordsRepository.downloadWords(
                            userDetail.preference.dailyWordQuota,
                            userDetail.vocabStats.currentWords
                        )

                        userRepository.saveUserDetail(userDetail)
                            .onSuccess {
                                scheduleAllNotifications(preference = userDetail.preference)
                                successfulLogin(userDetail.vocabStats.currentWords)
                            }

                    }
                }
            }
        }
    }

    suspend fun successfulLogin(words: List<String>) {
        Timber.d("inside successful logins $words")
        wordsRepository.updateWordStatusToAssigned(words)

        _loginState.value = LoginUiState.LoggedIn

    }

    private fun scheduleAllNotifications(preference: UserPreference) {
        notificationRepository.scheduleNotifications(
            preference.quizNotificationTimes,
            NotificationType.QUIZ_REMINDER
        )
        notificationRepository.scheduleNotifications(
            preference.wordsReminder,
            NotificationType.WORD_REMINDER
        )
        notificationRepository.scheduleNotification(
            preference.newWordsNotificationTime,
            NotificationType.WORD_REMINDER
        )
    }

}