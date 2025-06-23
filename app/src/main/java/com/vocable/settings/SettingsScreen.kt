package com.vocable.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utils.TimeAndDateUtils
import com.utils.TimeAndDateUtils.formatTimeForClockDesign
import com.vocable.R
import com.vocable.home.randomColor
import com.vocable.ui.theme.TealPrimary
import com.vocable.util.VocabDialog
import com.vocable.util.VocableTimePicker
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onNavigateUp: () -> Unit) {
    val viewmodel = koinViewModel<SettingsViewModel>()
    val state by viewmodel.state.collectAsState()

    state.timePicker?.let {
            VocableTimePicker(Modifier, onCancelled = {
                viewmodel.clearTimePickerType()
            }, onTimePicked = {
                viewmodel.updateNotificationTime(it)
            })
        }

    state.dialogType?.let {
        val (title, description) = when (it) {
            DialogType.NEW_WORD_INFO -> Pair(
                stringResource(R.string.info_new_words_reminder_title),
                stringResource(R.string.info_new_words_reminder_body)
            )

            DialogType.WORD_REMINDER_INFO -> Pair(
                stringResource(R.string.info_words_reminder_title),
                stringResource(R.string.info_words_reminder_body)
            )

            DialogType.WORD_COUNT_INFO -> Pair(
                stringResource(R.string.info_daily_word_count_title),
                stringResource(R.string.info_daily_word_count_body)
            )

            DialogType.WORD_COUNT_MORE_ERROR -> Pair(
                stringResource(R.string.error_daily_word_count_more_title),
                stringResource(R.string.error_daily_word_count_more_body)
            )

            DialogType.WORD_COUNT_LESS_ERROR -> Pair(
                stringResource(R.string.error_daily_word_count_less_title),
                stringResource(R.string.error_daily_word_count_less_body)
            )
        }
        VocabDialog(title, description) { viewmodel.clearDialog() }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {

        Row(

            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            IconButton(onClick = {
                onNavigateUp()
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Back"
                )
            }
            Text("Settings", style = MaterialTheme.typography.bodyMedium)
        }


            state.userDetail?.preference?.let { preference ->

                state.userDetail?.preference?.dailyWordQuota?.let { quota ->

                    Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
                        val isDarkTheme = isSystemInDarkTheme()
                        val color = randomColor(isDarkTheme)
                        Column(Modifier.fillMaxWidth(.5f)) {
                            Text(
                                stringResource(R.string.info_daily_word_count_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                stringResource(R.string.info_daily_word_count_body),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(150.dp)
                                .padding(start = 24.dp),
                            colors = CardDefaults.cardColors(containerColor = color),
                        ) {
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(start = 16.dp)
                            ) {
                                Text(
                                    text = quota.toString(),
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp),
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 82.sp), // Big size for hour
                                )


                                Column(
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxHeight()
                                ) {

                                    IconButton(
                                        modifier = Modifier
                                            .background(
                                                Color.Red.copy(alpha = .1f),
                                                shape = RoundedCornerShape(bottomStart = 12.dp)
                                            ),

                                        onClick = {
                                            if (quota <= 5) {
                                                viewmodel.updateWordsQuota(quota + 1)
                                            } else {
                                                viewmodel.showDialog(DialogType.WORD_COUNT_MORE_ERROR)
                                            }
                                        },
                                    ) {
                                        Icon(
                                            contentDescription = "Add",
                                            imageVector = ImageVector.vectorResource(R.drawable.ic_add),
                                        )
                                    }

                                    IconButton(
                                        modifier = Modifier
                                            .background(
                                                Color.Red.copy(alpha = .1f),
                                                shape = RoundedCornerShape(topStart = 12.dp)
                                            ),
                                        onClick = {
                                            if (quota > 1) {
                                                viewmodel.updateWordsQuota(quota - 1)
                                            } else {
                                                viewmodel.showDialog(DialogType.WORD_COUNT_LESS_ERROR)
                                            }
                                        },
                                    ) {
                                        Icon(
                                            contentDescription = "Subtract",
                                            imageVector = ImageVector.vectorResource(R.drawable.ic_minus),
                                        )

                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = TealPrimary)
                    Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
                        val isDarkTheme = isSystemInDarkTheme()
                        val color = randomColor(isDarkTheme)

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth(.5f)
                                .height(150.dp)
                                .padding(end = 24.dp),

                            colors = CardDefaults.cardColors(containerColor = color),
                        ) {
                            val time =
                                TimeAndDateUtils.convertUtcToLocalTime(preference.newWordsNotificationTime)
                            ClockTextDisplay(time) {
                                viewmodel.showTimePicker(TimePickerType.NEW_WORDS_TIME, 0)
                            }
                        }
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                stringResource(R.string.info_new_words_reminder_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                stringResource(R.string.info_new_words_reminder_body),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                HorizontalDivider(color = TealPrimary)
                TimeSettings(
                    text = stringResource(R.string.words_reminder_notification_time),
                    times = (preference.wordsReminder),
                    onTimeUpdated = { index ->
                        viewmodel.showTimePicker(TimePickerType.WORD_REMINDER_TIME, index)
                    },
                    infoClicked = { viewmodel.showDialog(DialogType.WORD_REMINDER_INFO) }
                )
            }
    }

}

@Composable
fun TimeSettings(
    infoClicked: () -> Unit,
    onTimeUpdated: (index: Int) -> Unit,
    text: String,
    times: List<Long>
) {
    Column(Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {

        Text(
            stringResource(R.string.info_words_reminder_title),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            stringResource(R.string.info_words_reminder_body),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            times.mapIndexed { index, time ->
                item {
                    val isDarkTheme = isSystemInDarkTheme()
                    val color = randomColor(isDarkTheme)
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(150.dp),
                        colors = CardDefaults.cardColors(containerColor = color),
                    ) {
                        val time = TimeAndDateUtils.convertUtcToLocalTime(time)
                        ClockTextDisplay(time) {
                            onTimeUpdated(index)
                        }
                    }
                }

            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    //  HorizontalDivider()
}


@Composable
fun ClockTextDisplay(time: String, onTimeUpdated: () -> Unit) {
    val (hour, minute, amPm) = formatTimeForClockDesign(time)
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = hour,
            modifier = Modifier
                .padding(start = 16.dp),
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 82.sp), // Big size for hour
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = {
                    onTimeUpdated()
                }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Time"
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = minute,
                    style = MaterialTheme.typography.titleMedium, // Smaller size for minute
                )

                Text(
                    text = amPm,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}










