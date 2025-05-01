package com.vocable.data.quiz.domain.repository

import com.vocable.data.quiz.domain.model.Quiz
import com.vocable.data.quiz.domain.model.QuizType
import com.vocable.data.word.domain.model.Word
import com.vocable.data.word.domain.model.WordStatus
import java.util.UUID

class QuizRepositoryImpl : QuizRepository {
    override fun generateQuizQuestions(words: List<Word>): List<Quiz> {
        val quizzes = mutableListOf<Quiz>()

        val currentWords = words.filter { it.status == WordStatus.Assigned }
        val learnedWords = words.filter { it.status == WordStatus.Learned }

        val input = Quiz(
            id = UUID.randomUUID().toString(),
            question = "Write all the words you learned today",
            answers = currentWords.map { it.word },
            correctAnswer = "",
            quizType = QuizType.USER_INPUT
        )
        quizzes.add(input)

        if (learnedWords.size > 1) {
            val selectedWords = learnedWords.shuffled().take(minOf(learnedWords.size, 3))

            selectedWords.forEach { wordItem ->
                val correctWord = wordItem.word
                val quizOptions = mutableListOf<String>().apply {
                    add(correctWord)
                    addAll(generateMisspellings(correctWord)) // Add incorrect ones
                }

                val quiz = Quiz(
                    id = UUID.randomUUID().toString(),
                    question = "Select correct Spelling",
                    answers = quizOptions.shuffled(),
                    quizType = QuizType.CHOICE,
                    correctAnswer = correctWord
                )
                quizzes.add(quiz)
            }
        }


        val randomWords = words.shuffled().take(4)
        val correctWord = words[0].word
        val correctMeaning = words[0].meaning

        val meaning = Quiz(
            id = UUID.randomUUID().toString(),
            question = "Select correct meaning of the word $correctWord",
            answers =arrayListOf(),
            quizType = QuizType.CHOICE,
            correctAnswer = ""
        )
        quizzes.add(meaning)

        return quizzes

    }

    fun generateMisspellings(word: String, count: Int = 3): List<String> {
        val typos = mutableSetOf<String>()

        val vowels = listOf('a', 'e', 'i', 'o', 'u')

        while (typos.size < count) {
            val typo = when ((1..4).random()) {
                1 -> removeRandomChar(word)
                2 -> swapAdjacentLetters(word)
                3 -> replaceVowel(word, vowels)
                else -> duplicateRandomChar(word)
            }

            if (typo != word) {
                typos.add(typo)
            }
        }

        return typos.toList()
    }

    fun removeRandomChar(word: String): String {
        if (word.length <= 1) return word
        val index = (word.indices).random()
        return word.removeRange(index, index + 1)
    }

    fun swapAdjacentLetters(word: String): String {
        if (word.length < 2) return word
        val index = (0 until word.length - 1).random()
        val chars = word.toCharArray()
        val temp = chars[index]
        chars[index] = chars[index + 1]
        chars[index + 1] = temp
        return String(chars)
    }

    fun replaceVowel(word: String, vowels: List<Char>): String {
        val indices = word.indices.filter { word[it] in vowels }
        if (indices.isEmpty()) return word
        val index = indices.random()
        val replacement = vowels.filterNot { it == word[index] }.random()
        return word.substring(0, index) + replacement + word.substring(index + 1)
    }

    fun duplicateRandomChar(word: String): String {
        val index = (word.indices).random()
        return word.substring(0, index + 1) + word[index] + word.substring(index + 1)
    }

}