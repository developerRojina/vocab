package com.vocable.profile

import com.vocable.data.user.domain.model.AppUserDetail
import com.vocable.data.word.domain.model.Word

data class ProfileUIState(
    val detail: AppUserDetail? = null,
    val wordsInfo: HashMap<WordsInfoType, List<Word>> = HashMap(),
    val countsInfo: List<Pair<WordsInfoType, Int>> = emptyList(),
    val selectedWordsInfoType: WordsInfoType = WordsInfoType.CURRENT
)

enum class WordsInfoType {
    LEARNED, CURRENT, QUIZ
}