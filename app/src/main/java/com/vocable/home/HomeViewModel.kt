package com.vocable.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocable.data.user.domain.repository.UserRepository
import com.vocable.data.word.domain.model.Word
import com.vocable.data.word.domain.repository.WordsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(wordsRepository: WordsRepository, userRepository: UserRepository) :
    ViewModel() {
    private val _state = MutableStateFlow<HomeUIState>(HomeUIState())
    val state: StateFlow<HomeUIState> get() = _state

    // Keep track of flash card states for each word
    private val wordFlashCardStates = mutableMapOf<String, WordFlashCardState>()

    init {
        viewModelScope.launch {
            val user = userRepository.getMyDetail()
            user.collect {
                it?.let {
                    val wordIds = it.vocabStats.currentWords
                    val words = wordIds.filter { it.isNotEmpty() }
                        .mapNotNull { wordsRepository.getWordDetail(it) }
                    _state.value = _state.value.copy(
                        words = words,
                        currentPage = 0,
                        detail = it,
                    )

                    // Initialize with first word if available
                    if (words.isNotEmpty()) {
                        updatePageData(0)
                    }
                }
            }
        }
    }

    fun updatePageData(page: Int) {
        val currentWord = state.value.words[page]
        val wordId = currentWord.id // Assuming Word has an ID

        // Get or create word-specific flash card state
        val flashCardState = wordFlashCardStates.getOrPut(wordId) {
            WordFlashCardState(getAvailableFlashCardTypes(currentWord))
        }

        // Get items based on current selection
        val selectedType = flashCardState.selectedFlashCardType
        val items = getFlashCardItems(currentWord, selectedType)

        _state.value = _state.value.copy(
            currentPage = page,
            selectedPageData = SelectedPageData(
                word = currentWord,
                availableFlashCars = flashCardState.availableTypes,
                flashCardItems = items,
                flashCardTypeWithCardIndex = Pair(selectedType, flashCardState.selectedCardIndex)
            ),
        )
    }

    fun selectFlashCardType(type: FlashCardType) {
        val currentWord = state.value.selectedPageData?.word ?: return
        val wordId = currentWord.id

        // Update the selection for this word
        wordFlashCardStates[wordId]?.let { wordState ->
            wordState.selectedFlashCardType = type
            wordState.selectedCardIndex = 0 // Reset card index when type changes

            // Get new items based on selection
            val items = getFlashCardItems(currentWord, type)

            _state.value = _state.value.copy(
                selectedPageData = state.value.selectedPageData?.copy(
                    flashCardTypeWithCardIndex = Pair(type, 0),
                    flashCardItems = items
                )
            )
        }
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

    // Class to hold state for each word's flash cards
    private class WordFlashCardState(
        val availableTypes: List<FlashCardType>
    ) {
        var selectedFlashCardType: FlashCardType =
            if (availableTypes.contains(FlashCardType.MEANING))
                FlashCardType.MEANING
            else
                availableTypes.firstOrNull() ?: FlashCardType.MEANING

        var selectedCardIndex: Int = 0
    }
}