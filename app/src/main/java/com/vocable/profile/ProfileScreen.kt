package com.vocable.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.vocable.WordCard
import com.vocable.data.word.domain.model.Word
import com.vocable.ui.theme.Gray
import com.vocable.ui.theme.SurfaceDark
import com.vocable.ui.theme.SurfaceLight
import com.vocable.ui.theme.TealPrimary
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToWordDetail: (wordId: String) -> Unit,
    onNavigateUp: () -> Unit
) {
    val viewmodel = koinViewModel<ProfileViewModel>()
    val state by viewmodel.state.collectAsState()
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        state.let { detail ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {


                IconButton(onClick = {
                    onNavigateUp()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    detail.detail?.displayName ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )



            }

            TabBarPage(
                modifier = Modifier.fillMaxWidth(), state = state, onNavigate = { wordInfo ->
                    when (wordInfo) {
                        WordsInfoType.LEARNED -> {

                        }

                        WordsInfoType.CURRENT -> {}
                        WordsInfoType.QUIZ -> {
                            onNavigateToQuiz()
                        }
                    }

                },
                onNavigateToWordDetail = { wordId -> onNavigateToWordDetail(wordId) }
            )
        }

    }
}



@Composable
fun WordList(
    wordsInfoType: WordsInfoType,
    onActionClicked: () -> Unit,
    onWordClicked: (wordId: String) -> Unit,
    words: List<Word>?
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp)
        ) {
            words?.forEachIndexed { index, word ->
                item {
                    WordCard(
                        content = word.word,
                        modifier = Modifier.fillMaxSize()  // Yellow
                    ) {
                        onWordClicked(word.id)
                    }

                }

            }
            if (wordsInfoType == WordsInfoType.QUIZ)
                item {
                    Button(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .fillMaxWidth(),
                        onClick = {
                            onActionClicked()
                        }) {

                        Text("Take Quiz", style = MaterialTheme.typography.bodyMedium)
                    }
                }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabBarPage(
    modifier: Modifier,
    state: ProfileUIState,
    onNavigate: (wordInfoType: WordsInfoType) -> Unit,
    onNavigateToWordDetail: (wordId: String) -> Unit,
) {
    val tabs = listOf(WordsInfoType.LEARNED, WordsInfoType.CURRENT)
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxWidth()

    ) {
        TabRow(
            containerColor = Color.Transparent,
            selectedTabIndex = pagerState.currentPage,
            indicator = {},
            divider = {},
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = TealPrimary.copy(alpha = .1f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(4.dp)

        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = pagerState.currentPage == index
                val tabColor = if (isSelected) TealPrimary else Color.Transparent
                val textColor = if (isSelected)  SurfaceLight else Gray

                Tab(
                    selected = isSelected,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(tabColor)
                ) {
                    Text(
                        text = title.name + " WORDS",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 4.dp),
                        color = textColor
                    )
                }
            }
        }
        HorizontalPager(
            count = tabs.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()

        ) { page ->
            when (page) {
                0 -> WordList(
                    WordsInfoType.LEARNED,
                    { onNavigate(WordsInfoType.LEARNED) },
                    { wordId -> onNavigateToWordDetail(wordId) },
                    state.wordsInfo[WordsInfoType.LEARNED]
                )

                1 -> WordList(
                    WordsInfoType.CURRENT,
                    { onNavigate(WordsInfoType.CURRENT) },
                    { wordId -> onNavigateToWordDetail(wordId) },
                    state.wordsInfo[WordsInfoType.CURRENT]
                )

                /* 2 -> WordList(
                     WordsInfoType.QUIZ,
                     { onNavigate(WordsInfoType.QUIZ) },
                     state.wordsInfo[WordsInfoType.QUIZ]
                 )*/
            }
        }
    }
}

