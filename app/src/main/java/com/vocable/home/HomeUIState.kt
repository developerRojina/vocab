package com.vocable.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.vocable.data.user.domain.model.AppUserDetail
import com.vocable.data.word.domain.model.Word
import com.vocable.ui.theme.Blue
import com.vocable.ui.theme.Brown
import com.vocable.ui.theme.Gray
import com.vocable.ui.theme.Green
import com.vocable.ui.theme.Orange
import com.vocable.ui.theme.Pink
import com.vocable.ui.theme.Purple
import com.vocable.ui.theme.Red
import com.vocable.ui.theme.TealPrimary
import com.vocable.ui.theme.Yellow

data class HomeUIState(
    val detail: AppUserDetail? = null,
    val pages: List<PageData> = emptyList(),
    val currentPage: Int = 0,
)

enum class FlashCardType(val color: Color) {
    MEANING(TealPrimary), SENTENCES(Orange), SYNONYMS(Yellow), ANTONYMS(Red), HYPERNYMS(Blue), CONTEXTS(
        Purple
    ),
    EQUIVALENTS(Brown), FORMS(Green), RELATED_WORDS(Pink), RHYMES(Gray)
}

data class PageData(
    val availableFlashCars: List<FlashCardType> = emptyList<FlashCardType>(),
    val flashCardTypeWithCardIndex: Pair<FlashCardType, Int>,
    val flashCardItems: List<Any>,
    val word: Word,)

data class FlashCardInfo(
    val data: String,
    val type: String?,
    val color: Color,
    val xOffset: Dp,
    val yOffset: Dp,
    val dragOffset: Float
)

