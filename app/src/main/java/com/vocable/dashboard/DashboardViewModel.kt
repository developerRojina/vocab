package com.vocable.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocable.data.user.domain.repository.UserRepository
import com.vocable.data.word.domain.model.Word
import com.vocable.data.word.domain.repository.WordsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(wordsRepository: WordsRepository, userRepository: UserRepository) :
    ViewModel() {
    private val _myWords = MutableStateFlow<List<Word>>(emptyList())
    val myWords: StateFlow<List<Word>> get() = _myWords

    init {

        viewModelScope.launch {
            val user = userRepository.getMyDetail()
            user.collect {
                it?.let {
                    val wordIds = it.vocabStats.currentWords
                    _myWords.value =
                        wordIds.mapNotNull { wordsRepository.getWordDetail(it) }
                }
            }
        }
    }
}