package com.vocable.quiz

import com.vocable.data.quiz.domain.model.Quiz

data class QuizzesUiState(
    var quizzes: List<Quiz> = emptyList<Quiz>(),
    val score: Int = 0, )



data class QuizUiState(
    var quiz: Quiz,
    var remainingTimeInSec: Int,

)