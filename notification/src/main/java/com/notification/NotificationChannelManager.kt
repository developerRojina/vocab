package com.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import com.notification.Constants.CHANNEL_DESCRIPTION_NEW_WORDS
import com.notification.Constants.CHANNEL_DESCRIPTION_QUIZ
import com.notification.Constants.CHANNEL_DESCRIPTION_WORDS_REMINDER
import com.notification.Constants.CHANNEL_ID_NEW_WORDS
import com.notification.Constants.CHANNEL_ID_QUIZ
import com.notification.Constants.CHANNEL_ID_WORDS_REMINDER
import com.notification.Constants.CHANNEL_NAME_NEW_WORDS
import com.notification.Constants.CHANNEL_NAME_QUIZ
import com.notification.Constants.CHANNEL_NAME_WORDS_REMINDER

class NotificationChannelManager {


    val notificationChannels =
        hashMapOf<String, Pair<String, String>>(
            CHANNEL_ID_QUIZ to Pair(CHANNEL_NAME_QUIZ, CHANNEL_DESCRIPTION_QUIZ),
            CHANNEL_ID_NEW_WORDS to Pair(CHANNEL_NAME_NEW_WORDS, CHANNEL_DESCRIPTION_NEW_WORDS),
            CHANNEL_ID_WORDS_REMINDER to Pair(
                CHANNEL_NAME_WORDS_REMINDER,
                CHANNEL_DESCRIPTION_WORDS_REMINDER
            )
        )


    fun createNotificationChannels(manager: NotificationManager) {
        val channels = notificationChannels.map {
            NotificationChannel(
                it.key,
                it.value.first,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = it.value.second
            }
        }

        manager.createNotificationChannels(channels)
    }


}