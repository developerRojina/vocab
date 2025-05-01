package com.notification.domain.repository

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.notification.Constants.CHANNEL_ID_NEW_WORDS
import com.notification.Constants.CHANNEL_ID_QUIZ
import com.notification.Constants.CHANNEL_ID_WORDS_REMINDER
import com.notification.NotificationChannelManager
import com.notification.domain.model.NotificationItem
import com.notification.domain.model.NotificationType
import com.notification.scheduler.LocalNotificationScheduler
import com.utils.TimeAndDateUtils

class NotificationRepositoryImpl(
    val context: Context,
    val notificationScheduler: LocalNotificationScheduler,
    notificationChannelManager: NotificationChannelManager
) :
    NotificationRepository {

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        notificationChannelManager.createNotificationChannels(notificationManager)
    }

    override fun scheduleNotifications(items: List<Long>, type: NotificationType) {
        items.map {
            var time = it
            if (TimeAndDateUtils.hasTimePassed(it)) {
                time = TimeAndDateUtils.addDayToTimestamp(it)
            }
            notificationScheduler.schedule(
                NotificationItem(
                    id = it.hashCode(),
                    time = time,
                    notificationType = type,
                )
            )
        }
    }

    override fun scheduleNotification(item: Long, type: NotificationType) {
        var time = item
        if (TimeAndDateUtils.hasTimePassed(item)) {
            time = TimeAndDateUtils.addDayToTimestamp(item)
        }
        notificationScheduler.schedule(
            NotificationItem(
                id = item.hashCode(),
                time = time,
                notificationType = type,
            )
        )
    }

    override fun updateNotificationTime(
        item: Long,
        type: NotificationType
    ) {
        cancelNotification(item.hashCode())
        scheduleNotification(item, type)
    }

    override fun cancelNotification(id: Int) {
        notificationScheduler.cancel(id)
    }


    override fun showNotification(
        notificationType: NotificationType,
        notificationId: Int,
        contentIntent: Intent
    ) {
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId, // Use the same request code here for consistency
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification based on notification type
        val builder =
            NotificationCompat.Builder(context, getNotificationChannelId(notificationType))
                .setSmallIcon(getNotificationIcon(notificationType))
                .setContentTitle(getNotificationTitle(notificationType))
                .setContentText(getNotificationMessage(notificationType))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        // Show the notification with the same ID as the request code
        notificationManager.notify(notificationId, builder.build())
    }

    private fun getNotificationIcon(notificationType: NotificationType): Int {
        return when (notificationType) {
            NotificationType.NEW_WORDS -> android.R.drawable.ic_notification_overlay
            NotificationType.QUIZ_REMINDER -> android.R.drawable.ic_notification_overlay
            NotificationType.WORD_REMINDER -> android.R.drawable.ic_notification_overlay
        }
    }

    private fun getNotificationTitle(notificationType: NotificationType): String {
        return when (notificationType) {
            NotificationType.NEW_WORDS -> "New Words Available"
            NotificationType.QUIZ_REMINDER -> "Time for a Quiz!"
            NotificationType.WORD_REMINDER -> "Word Practice Reminder"
        }
    }

    private fun getNotificationMessage(notificationType: NotificationType): String {
        return when (notificationType) {
            NotificationType.NEW_WORDS -> "New Words Available"
            NotificationType.QUIZ_REMINDER -> "Time for a Quiz!"
            NotificationType.WORD_REMINDER -> "Word Practice Reminder"
        }
    }

    private fun getNotificationChannelId(notificationType: NotificationType): String {
        return when (notificationType) {
            NotificationType.NEW_WORDS -> CHANNEL_ID_NEW_WORDS
            NotificationType.QUIZ_REMINDER -> CHANNEL_ID_QUIZ
            NotificationType.WORD_REMINDER -> CHANNEL_ID_WORDS_REMINDER
        }
    }

}