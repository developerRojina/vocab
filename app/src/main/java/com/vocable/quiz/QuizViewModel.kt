package com.vocable.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocable.data.quiz.domain.repository.QuizRepository
import com.vocable.data.user.domain.repository.UserRepository
import com.vocable.data.word.domain.model.Word
import com.vocable.data.word.domain.repository.WordsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel(
    userRepository: UserRepository,
    wordRepository: WordsRepository,
    quizRepository: QuizRepository
) : ViewModel() {
    private val _state = MutableStateFlow<QuizzesUiState>(QuizzesUiState())
    val state: StateFlow<QuizzesUiState> get() = _state

    val QUESTION_DURATION = 5_000L

    init {
        viewModelScope.launch {
            userRepository.getMyDetail().collect { detail ->
                detail?.let {
                    detail.vocabStats.let { vocabStats ->
                        val learnedWords = vocabStats.learnedWords
                        val assignedWords = vocabStats.currentWords

                        val allWords = mutableListOf<Word>()

                        if (learnedWords.size > 1)
                            allWords.addAll(learnedWords.mapNotNull {
                                wordRepository.getWordDetail(
                                    it
                                )
                            })


                        allWords.addAll(assignedWords.mapNotNull { wordRepository.getWordDetail(it) })

                        _state.value =
                            _state.value.copy(
                                quizzes = quizRepository.generateQuizQuestions(
                                    allWords
                                )
                            )
                    }
                }

            }
        }

    }

    fun startQuiz() {

    }

    fun increaseScore() {


    }


}