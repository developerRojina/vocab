package com.vocable.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.utils.TimeAndDateUtils
import com.vocable.R
import com.vocable.ui.theme.TealPrimary
import com.vocable.util.VocableTimePicker
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val viewmodel = koinViewModel<SettingsViewModel>()
    val state by viewmodel.state.collectAsState()

    Scaffold(
        topBar = {

        }
    ) { padding ->

        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = TealPrimary),
            modifier = Modifier
                .size(50.dp)

        ) {
            IconButton(
                onClick = { },
            ) {
                Text(text = state.userDetail?.preference?.dailyWordQuota?.toString() ?: "")
            }
        }


        if (state.timePicker != null) {
            VocableTimePicker(Modifier, onCancelled = {
                viewmodel.clearTimePickerType()
            }, onTimePicked = {
                viewmodel.updateNotificationTime(it)
            })
        }



        Column(modifier = Modifier.padding(padding)) {
            state.userDetail?.preference?.let { preference ->
                Text(stringResource(R.string.daily_word_quota))

                Row {

                    Text(preference.dailyWordQuota.toString())
                }

                Text(stringResource(R.string.new_word_notification_time))

                //  val time =
                        TimeAndDateUtils.convertUtcToLocalTime(preference.newWordsNotificationTime)
                // Text(text = time)

                TimeSettings(
                    text = stringResource(R.string.new_word_notification_time),
                    times = listOf(preference.newWordsNotificationTime),
                    onTimeUpdated = { index ->
                        viewmodel.showTimePicker(TimePickerType.NEW_WORDS_TIME, index)
                    })

                TimeSettings(
                    text = stringResource(R.string.words_reminder_notification_time),
                    times = (preference.wordsReminder),
                    onTimeUpdated = { index ->
                        viewmodel.showTimePicker(TimePickerType.WORD_REMINDER_TIME, index)
                    })

                TimeSettings(
                    text = stringResource(R.string.quiz_notification_time),
                    times = (preference.quizNotificationTimes),
                    onTimeUpdated = { index ->
                        viewmodel.showTimePicker(TimePickerType.QUIZ_REMINDER_TIME, index)
                    })


                Text(preference.selectedTheme.toString())
            }


        }
    }
}

@Composable
fun ColumnScope.TimeSettings(onTimeUpdated: (index: Int) -> Unit, text: String, times: List<Long>) {
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
                                onTimeUpdated(index)
                                //    viewmodel.showTimePicker(TimePickerType.WORD_REMINDER_TIME, index)
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








