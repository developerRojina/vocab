package com.vocable.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocable.data.word.domain.model.Word
import com.vocable.data.word.domain.repository.WordsRepository
import com.vocable.home.FlashCardType
import com.vocable.home.HomeUIState
import com.vocable.home.PageData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WordDetailViewModel(val wordsRepository: WordsRepository) : ViewModel() {

    private val _state = MutableStateFlow<WordDetailUIState>(WordDetailUIState())
    val state: StateFlow<WordDetailUIState> get() = _state

    fun getWordDetail(wordId: String) {
        viewModelScope.launch {
            wordsRepository.getWordDetail(wordId)
                ?.let { word ->

                    val selectedType = FlashCardType.MEANING
                    val items = getFlashCardItems(word, selectedType)
                    val pageData = PageData(
                        word = word,
                        availableFlashCars = getAvailableFlashCardTypes(word),
                        flashCardItems = items,
                        flashCardTypeWithCardIndex = Pair(
                            selectedType,
                            0
                        )
                    )

                    _state.value = _state.value.copy(
                        page = pageData
                    )
                }
        }

    }

    private fun getFlashCardItems(word: Word, type: FlashCardType): List<Any> {
        return when (type) {
            FlashCardType.FORMS -> word.forms
            FlashCardType.RHYMES -> word.rhymes
            FlashCardType.MEANING -> word.meaning
            FlashCardType.SENTENCES -> word.sentences
            FlashCardType.SYNONYMS -> word.synonyms
            FlashCardType.ANTONYMS -> word.antonyms
            FlashCardType.HYPERNYMS -> word.hypernyms
            FlashCardType.CONTEXTS -> word.contexts
            FlashCardType.EQUIVALENTS -> word.equivalents
            FlashCardType.RELATED_WORDS -> word.equivalents
        } ?: emptyList()
    }

    private fun getAvailableFlashCardTypes(word: Word): List<FlashCardType> {
        return buildList {
            if (word.meaning.isNotEmpty()) add(FlashCardType.MEANING)
            if (word.antonyms?.isNotEmpty() == true) add(FlashCardType.ANTONYMS)
            if (word.synonyms?.isNotEmpty() == true) add(FlashCardType.SYNONYMS)
            if (word.contexts?.isNotEmpty() == true) add(FlashCardType.CONTEXTS)
            if (word.equivalents?.isNotEmpty() == true) add(FlashCardType.EQUIVALENTS)
            if (word.sentences?.isNotEmpty() == true) add(FlashCardType.SENTENCES)
            if (word.etymologicallyRelatedWords?.isNotEmpty() == true) add(FlashCardType.RELATED_WORDS)
            if (word.hypernyms?.isNotEmpty() == true) add(FlashCardType.HYPERNYMS)
            if (word.forms?.isNotEmpty() == true) add(FlashCardType.FORMS)
            if (word.rhymes?.isNotEmpty() == true) add(FlashCardType.RHYMES)
        }
    }
}