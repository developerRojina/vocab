package com.vocable.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utils.TimeAndDateUtils
import com.vocable.R
import com.vocable.data.word.domain.model.Word
import com.vocable.ui.theme.TealPrimary
import com.vocable.ui.theme.TealPrimaryVariant
import com.vocable.ui.theme.TealVariant
import org.koin.androidx.compose.koinViewModel

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


                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = TealPrimary),
                    modifier = Modifier
                        .size(50.dp)

                ) {
                    IconButton(
                        onClick = { },
                    ) {
                        Text(text = state.detail?.preference?.dailyWordQuota?.toString() ?: "")
                    }
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



            state.detail?.preference?.newWordsNotificationTime?.let {
                TimeSettings(stringResource(R.string.new_word_notification_time), listOf(it))
            }
            state.detail?.preference?.wordsReminder?.let {
                TimeSettings(stringResource(R.string.words_reminder_notification_time), it)
            }
            state.detail?.preference?.quizNotificationTimes?.let {
                TimeSettings(stringResource(R.string.quiz_notification_time), it)
            }



            Box() {

                Box {
                    Card(
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .height(90.dp)
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape( topStart = 58.dp,  topEnd = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = TealVariant)
                    ) {

                        Text(text = WordsInfoType.CURRENT.name, textAlign = TextAlign.Center, modifier = Modifier.fillMaxSize())
                    }
                    Card(
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .height(60.dp)
                            .width(180.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 58.dp),
                        colors = CardDefaults.cardColors(containerColor = TealPrimary)
                    ) {

                        Text(
                            text = WordsInfoType.CURRENT.name,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxSize()
                        )
                    }


                }

                Card(
                    modifier = Modifier
                        .padding(top = 80.dp)
                        .fillMaxWidth()
                        .height(300.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(
                        bottomEnd = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = 20.dp
                    ),
                    colors = CardDefaults.cardColors(containerColor = TealPrimary)
                ) {

                }


            }
            /*    Box(modifier = Modifier.fillMaxSize()) {
                 LeaderboardPodium(Modifier.height(300.dp), detail.wordsInfo)
                detail.wordsInfo[state.selectedWordsInfoType]?.let {
                       if (it.isNotEmpty())
                           WordList(it)
                   }
        }
*/


        }

    }
}

@Composable
fun ColumnScope.TimeSettings(text: String, times: List<Long>) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(text)
    LazyRow {
        times.mapIndexed { index, time ->
            item {
                Box(
                    Modifier
                        .wrapContentSize()
                        .border(
                            width = 2.dp,
                            shape = RoundedCornerShape(12.dp),
                            color = TealPrimary
                        )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        val time = TimeAndDateUtils.convertUtcToLocalTime(time)
                        Text(text = time)
                        IconButton(
                            onClick = {
                                //  viewmodel.showTimePicker(TimePickerType.WORD_REMINDER_TIME, index)
                            }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Time"
                            )
                        }
                    }
                }
            }
            item { Spacer(Modifier.width(16.dp)) }

        }
    }
}

@Composable
fun WordList(words: List<Word>) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 250.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = TealVariant)
    ) {

        Box(modifier = Modifier.padding(16.dp)) {
            val state = rememberLazyListState()
            LazyColumn {
                words.forEachIndexed { index, word ->
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


@Composable
fun LeaderboardPodium(modifier: Modifier, words: HashMap<WordsInfoType, List<Word>>) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {


            WordsInfoType.entries.forEachIndexed { index, info ->
                val item = words[info]
                PodiumItem(
                    name = info,
                    bgColor = TealPrimaryVariant,
                    height = minOf(60.dp * (item?.size ?: 1), 200.dp),
                    words = item ?: emptyList()
                )
            }
        }
    }
}


@Composable
fun PodiumItem(
    name: WordsInfoType,
    bgColor: Color,
    height: Dp,
    words: List<Word>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .width(100.dp)
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = name.name,
            color = TealVariant,
            style = MaterialTheme.typography.bodyMedium,
        )

        Box(
            modifier = Modifier
                .height(height)
                .fillMaxWidth()
                .background(bgColor, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn {
                item {
                    Text(
                        text = words.size.toString(),
                        color = Color(0xFFFFFFFF),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                words.map { word ->
                    item {
                        Text(
                            text = word.word + word.word,
                            color = Color(0xFFFFFFFF),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

            }


        }
    }
}