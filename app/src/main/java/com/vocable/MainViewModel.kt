package com.vocable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocable.data.auth.domain.repository.AuthRepository
import com.vocable.data.user.domain.model.AppUser
import com.vocable.data.user.domain.repository.UserRepository
import com.vocable.data.word.domain.repository.WordsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    authRepository: AuthRepository,
    val userRepository: UserRepository,
    val wordsRepository: WordsRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<AppUser?>(null)
    val currentUser: StateFlow<AppUser?> get() = _currentUser

    init {
        _currentUser.value = authRepository.getCurrentUser
    }

    fun updateWords() {
        viewModelScope.launch {
            val userDetail = userRepository.getMyDetail().first()
            userDetail?.let {
                val quota = userDetail.preference.dailyWordQuota
                val existingWords = userDetail.vocabStats.currentWords

                wordsRepository.updateWordStatusToLearned(existingWords)

                val words = wordsRepository.getWordsOfTheDay(quota).first()

                wordsRepository.updateWordStatusToAssigned(words)
                userRepository.updateLearnedWords(userDetail.vocabStats.currentWords)

                userRepository.updateCurrentWords(words)
            }

        }
    }
}