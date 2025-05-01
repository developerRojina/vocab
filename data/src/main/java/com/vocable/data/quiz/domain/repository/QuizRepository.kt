package com.vocable.data.quiz.domain.repository

import com.vocable.data.quiz.domain.model.Quiz
import com.vocable.data.word.domain.model.Word

interface QuizRepository {
    fun generateQuizQuestions(words: List<Word>): List<Quiz>
}