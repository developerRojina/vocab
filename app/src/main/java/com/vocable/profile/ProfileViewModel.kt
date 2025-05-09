package com.vocable.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocable.data.user.domain.repository.UserRepository
import com.vocable.data.word.domain.model.Word
import com.vocable.data.word.domain.repository.WordsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    val userRepository: UserRepository,
    val wordRepository: WordsRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileUIState>(ProfileUIState())
    val state: StateFlow<ProfileUIState> get() = _state


    init {
        viewModelScope.launch {
            val userDetail = userRepository.getMyDetail()
            userDetail.collect { detail ->
                detail?.let {
                    _state.value = _state.value.copy(detail = detail)

                    val items = mutableListOf<Pair<WordsInfoType, Int>>()
                    val wordsInfo = hashMapOf<WordsInfoType, List<Word>>()
                    items.add(Pair(WordsInfoType.LEARNED, detail.vocabStats.learnedWords.size))
                    items.add(Pair(WordsInfoType.CURRENT, detail.vocabStats.currentWords.size))
                    items.add(Pair(WordsInfoType.QUIZ, detail.vocabStats.currentWords.size))

                    if (detail.vocabStats.learnedWords.size > 1) {
                        val learnedWords =
                            detail.vocabStats.learnedWords.mapNotNull { wordRepository.getWordDetail(it) }
                        println("thw words are $learnedWords")

                        wordsInfo[WordsInfoType.LEARNED] = learnedWords
                    }
                    if (detail.vocabStats.quizzedWords.size > 1) {
                        val quizzedWords =
                            detail.vocabStats.quizzedWords.mapNotNull { wordRepository.getWordDetail(it) }
                        println("thw words are $quizzedWords")
                        wordsInfo[WordsInfoType.QUIZ] = quizzedWords
                    }


                    if (detail.vocabStats.currentWords.size > 1) {
                        val currentWords =
                            detail.vocabStats.currentWords.mapNotNull { wordRepository.getWordDetail(it) }
                        wordsInfo[WordsInfoType.CURRENT] = currentWords
                        println("thw words are $currentWords")
                    }

                    _state.value = _state.value.copy(wordsInfo = wordsInfo, countsInfo = items)

                }
            }
        }
    }
}