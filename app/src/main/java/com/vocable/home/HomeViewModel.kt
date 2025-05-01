package com.vocable.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocable.data.user.domain.repository.UserRepository
import com.vocable.data.word.domain.model.Word
import com.vocable.data.word.domain.repository.WordsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(wordsRepository: WordsRepository, userRepository: UserRepository) :
    ViewModel() {
    private val _myWords = MutableStateFlow<List<Word>>(emptyList())
    val myWords: StateFlow<List<Word>> get() = _myWords

    init {

        viewModelScope.launch {
            // wordsRepository.writeWordsInDb()
            val user = userRepository.getMyDetail()
            user.collect {
                it?.let {
                    val wordIds = it.vocabStats.currentWords
                    _myWords.value =
                        wordIds.filter { it.isNotEmpty() }
                            .mapNotNull { wordsRepository.getWordDetail(it) }
                }

            }
        }
    }
}