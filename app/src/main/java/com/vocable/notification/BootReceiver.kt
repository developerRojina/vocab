package com.vocable.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.vocable.data.user.domain.repository.UserRepository
import com.vocable.notification.domain.model.NotificationType
import com.vocable.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val koin = GlobalContext.get()
            val notificationRepository = koin.get<NotificationRepository>()
            val userRepository = koin.get<UserRepository>()

            CoroutineScope(Dispatchers.IO).launch {
                val detail = userRepository.getMyDetail().firstOrNull()
                detail?.preference?.let { preference ->
                    notificationRepository.scheduleNotifications(
                        preference.wordsReminder,
                        NotificationType.WORD_REMINDER
                    )
                    notificationRepository.scheduleNotification(
                        preference.newWordsNotificationTime,
                        NotificationType.NEW_WORDS
                    )
                }

            }
        }
    }
}