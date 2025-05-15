package com.vocable.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.vocable.data.word.domain.model.Word
import com.vocable.ui.theme.TealPrimary
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProfileScreen(onNavigateToSettings: () -> Unit, onNavigateToQuiz: () -> Unit) {
    val viewmodel = koinViewModel<ProfileViewModel>()
    val state by viewmodel.state.collectAsState()
    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        state.let { detail ->

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Column {
                    Text(detail.detail?.displayName ?: "")
                    Text(detail.detail?.email ?: "", style = MaterialTheme.typography.bodySmall)
                }

                IconButton(
                    onClick = { onNavigateToSettings() },

                    ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.Black // optional: change icon color
                    )
                }
            }

            TabBarPage(modifier = Modifier.fillMaxWidth(), state = state)

        }

    }
}



@Composable
fun WordList(words: List<Word>?) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        // colors = CardDefaults.cardColors(containerColor = randomColor())
    ) {

        Box(modifier = Modifier.padding(16.dp)) {
            val state = rememberLazyListState()
            LazyColumn {
                words?.forEachIndexed { index, word ->
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),

                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = White)
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 24.dp
                                )
                            ) {
                                Text(word.word)
                                Text(
                                    word.meaning[0].meaning,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                            }


                        }
                        Spacer(Modifier.height(16.dp))
                    }

                }
            }
        }

    }

}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabBarPage(modifier: Modifier, state: ProfileUIState) {
    val tabs = WordsInfoType.entries.toList()
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = TealPrimary.copy(alpha = .1f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(4.dp)
    ) {
        TabRow(
            containerColor = Color.Transparent,
            selectedTabIndex = pagerState.currentPage,
            indicator = {},
            divider = {},
            modifier = Modifier.fillMaxWidth()

        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = pagerState.currentPage == index
                val tabColor = if (isSelected) TealPrimary else Color.Transparent
                val textColor = if (isSelected) White else Color.Black

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
                        text = title.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                        color = textColor
                    )
                }
            }
        }
    }

    HorizontalPager(
        count = tabs.size,
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) { page ->
        when (page) {
            0 -> WordList(state.wordsInfo[WordsInfoType.LEARNED])
            1 -> WordList(state.wordsInfo[WordsInfoType.CURRENT])
            2 -> WordList(state.wordsInfo[WordsInfoType.QUIZ])
        }
    }

}

