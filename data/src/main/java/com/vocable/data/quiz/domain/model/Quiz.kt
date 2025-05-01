package com.vocable.data.quiz.domain.model

data class Quiz(
    val id: String,
    val answers: List<String>,
    val question: String,
    val correctAnswer: String,
    val quizType: QuizType
)

enum class QuizType {
    USER_INPUT,
    CHOICE
}