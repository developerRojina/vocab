package com.vocable.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.vocable.data.user.domain.model.AppUserDetail
import com.vocable.data.word.domain.model.Word

data class HomeUIState(
    val detail: AppUserDetail? = null,
    val words: List<Word> = emptyList(),
    val selectedPageData: SelectedPageData? = null,
    val currentPage: Int = 0,
)

enum class FlashCardType {
    MEANING, SENTENCES, SYNONYMS, ANTONYMS, HYPERNYMS, CONTEXTS, EQUIVALENTS, FORMS, RELATED_WORDS, RHYMES
}

data class SelectedPageData(
    val availableFlashCars: List<FlashCardType> = emptyList<FlashCardType>(),
    val flashCardTypeWithCardIndex: Pair<FlashCardType, Int>,
    val flashCardItems: List<Any>,
    val word: Word,

    )

data class FlashCardInfo(
    val data: String,
    val type: String?,
    val color: Color,
    val xOffset: Dp,
    val yOffset: Dp,
    val dragOffset: Float
)

data class CardState(val xOffset: Dp, val yOffset: Dp, val dragOffset: Float)

