package com.vocable.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.utils.TimeAndDateUtils
import com.vocable.R
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
                Row {
                    val time =
                        TimeAndDateUtils.convertUtcToLocalTime(preference.newWordsNotificationTime)
                    Text(text = time)
                    IconButton(
                        onClick = {
                            viewmodel.showTimePicker(TimePickerType.NEW_WORDS_TIME, 0)
                        }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Time"
                        )
                    }
                }


                Text(stringResource(R.string.words_reminder_notification_time))
                preference.wordsReminder.mapIndexed { index, time ->
                    Row {
                        val time = TimeAndDateUtils.convertUtcToLocalTime(time)
                        Text(text = time)
                        IconButton(
                            onClick = {
                                viewmodel.showTimePicker(TimePickerType.WORD_REMINDER_TIME, index)
                            }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Time"
                            )
                        }
                    }

                }


                Text(stringResource(R.string.quiz_notification_time))
                preference.quizNotificationTimes.mapIndexed { index, time ->

                    Row {
                        val time = TimeAndDateUtils.convertUtcToLocalTime(time)
                        Text(text = time)
                        IconButton(
                            onClick = {
                                viewmodel.showTimePicker(TimePickerType.QUIZ_REMINDER_TIME, index)
                            }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Time"
                            )
                        }
                    }

                }

                Text(preference.selectedTheme.toString())
            }


        }
    }
}







