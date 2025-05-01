package com.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.notification.Constants.EXTRA_NOTIFICATION_TIME
import com.notification.Constants.EXTRA_NOTIFICATION_TYPE
import com.notification.Constants.EXTRA_REQUEST_CODE
import com.notification.domain.model.NotificationType
import com.notification.domain.repository.NotificationRepository
import com.utils.TimeAndDateUtils
import org.koin.core.context.GlobalContext

/**
 * Receiver for handling notification alarms
 */
class LocalNotificationReceiver() : BroadcastReceiver() {

    lateinit var contentIntent: Intent
    lateinit var notificationRepository: NotificationRepository


    override fun onReceive(context: Context, intent: Intent) {
        val koin = GlobalContext.get()

        if (!::contentIntent.isInitialized && !::notificationRepository.isInitialized) {
            contentIntent = koin.get()
            notificationRepository = koin.get()

        }

        val notificationTypeName = intent.getStringExtra(EXTRA_NOTIFICATION_TYPE) ?: return
        val notificationType = try {
            NotificationType.valueOf(notificationTypeName)
        } catch (e: IllegalArgumentException) {
            return
        }
        val id = intent.getIntExtra(EXTRA_REQUEST_CODE, 0)
        val time = intent.getLongExtra(EXTRA_NOTIFICATION_TIME, 0)


        contentIntent.putExtra(EXTRA_NOTIFICATION_TYPE, notificationType.name)

        notificationRepository.showNotification(notificationType, id, contentIntent)

        notificationRepository.scheduleNotification(
            type = notificationType,
            item = TimeAndDateUtils.addDayToTimestamp(time)
        )

    }


}