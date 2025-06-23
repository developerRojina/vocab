package com.vocable.notification.domain.repository

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.utils.TimeAndDateUtils
import com.vocable.R
import com.vocable.notification.Constants.CHANNEL_ID_NEW_WORDS
import com.vocable.notification.Constants.CHANNEL_ID_QUIZ
import com.vocable.notification.Constants.CHANNEL_ID_WORDS_REMINDER
import com.vocable.notification.NotificationChannelManager
import com.vocable.notification.domain.model.NotificationContent
import com.vocable.notification.domain.model.NotificationItem
import com.vocable.notification.domain.model.NotificationType
import com.vocable.notification.scheduler.LocalNotificationScheduler
import timber.log.Timber

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
        print("Scheduling ${items.size} notifications")
        items.forEachIndexed { index, time ->
            var time = items[index]
            if (TimeAndDateUtils.hasTimePassed(time)) {
                time = TimeAndDateUtils.addDayToTimestamp(time)
            }
            scheduleNotification(time, type)
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
        notificationContent: NotificationContent
    ) {
        Timber.d("inside show notification of NotificationRepositoryImpl");
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationContent.notificationId, // Use the same request code here for consistency
            notificationContent.contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationType = notificationContent.notificationType
        // Build the notification based on notification type
        val builder =
            NotificationCompat.Builder(context, getNotificationChannelId(notificationType))
                .setSmallIcon(getNotificationIcon(notificationType))
                .setContentTitle(
                    notificationContent.notificationTitle ?: getNotificationTitle(
                        notificationType
                    )
                )
                .setContentText(
                    notificationContent.notificationContent ?: getNotificationMessage(
                        notificationType
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        // Show the notification with the same ID as the request code
        notificationManager.notify(notificationContent.notificationId, builder.build())
    }

    private fun getNotificationIcon(notificationType: NotificationType): Int {
        return when (notificationType) {
            NotificationType.NEW_WORDS -> R.drawable.ic_logo
            NotificationType.QUIZ_REMINDER -> R.drawable.ic_logo
            NotificationType.WORD_REMINDER -> R.drawable.ic_logo
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