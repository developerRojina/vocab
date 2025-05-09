package com.vocable.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocable.data.user.domain.repository.UserRepository
import com.vocable.data.word.domain.repository.WordsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(wordsRepository: WordsRepository, userRepository: UserRepository) :
    ViewModel() {
    private val _state = MutableStateFlow<HomeUIState>(HomeUIState())
    val state: StateFlow<HomeUIState> get() = _state

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

                }
            }
        }
    }

    fun updatePageData(page: Int) {
        val currentWord = state.value.words[page]
        Timber.d("the current word is $currentWord")
        val availableFlashCardTypes = buildList<FlashCardType> {
            if (currentWord.meaning.isNotEmpty())
                add(FlashCardType.MEANING)
            if (currentWord.antonyms?.isNotEmpty() == true)
                add(FlashCardType.ANTONYMS)
            if (currentWord.synonyms?.isNotEmpty() == true) {
                add(FlashCardType.SYNONYMS)
            }
            if (currentWord.contexts?.isNotEmpty() == true) {
                add(FlashCardType.CONTEXTS)
            }
            if (currentWord.equivalents?.isNotEmpty() == true) {
                add(FlashCardType.EQUIVALENTS)
            }
            if (currentWord.sentences?.isNotEmpty() == true) {
                add(FlashCardType.SENTENCES)
            }

            if (currentWord.etymologicallyRelatedWords?.isNotEmpty() == true) {
                add(FlashCardType.RELATED_WORDS)
            }
            if (currentWord.hypernyms?.isNotEmpty() == true) {
                add(FlashCardType.HYPERNYMS)
            }
            if (currentWord.forms?.isNotEmpty() == true) {
                add(FlashCardType.FORMS)
            }
            if (currentWord.rhymes?.isNotEmpty() == true) {
                add(FlashCardType.RHYMES)
            }

        }
        var selectedFlashCard = state.value.selectedPageData?.flashCardTypeWithCardIndex
        if (selectedFlashCard == null)
            selectedFlashCard = Pair(FlashCardType.MEANING, 0)

        val items = when (selectedFlashCard.first) {
            FlashCardType.FORMS -> currentWord.forms
            FlashCardType.RHYMES -> currentWord.rhymes
            FlashCardType.MEANING -> currentWord.meaning
            FlashCardType.SENTENCES -> currentWord.sentences
            FlashCardType.SYNONYMS -> currentWord.synonyms
            FlashCardType.ANTONYMS -> currentWord.antonyms
            FlashCardType.HYPERNYMS -> currentWord.hypernyms
            FlashCardType.CONTEXTS -> currentWord.contexts
            FlashCardType.EQUIVALENTS -> currentWord.equivalents
            FlashCardType.RELATED_WORDS -> currentWord.equivalents
        }

        _state.value = _state.value.copy(
            currentPage = page,
            selectedPageData = SelectedPageData(
                word = currentWord,
                availableFlashCars = availableFlashCardTypes,
                flashCardItems = items ?: emptyList(),
                flashCardTypeWithCardIndex = Pair(FlashCardType.MEANING, 0)
            ),
        )

    }
}