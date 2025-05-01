package com.notification.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.notification.Constants.EXTRA_NOTIFICATION_TIME
import com.notification.Constants.EXTRA_NOTIFICATION_TYPE
import com.notification.Constants.EXTRA_REQUEST_CODE
import com.notification.LocalNotificationReceiver
import com.notification.domain.model.NotificationItem

class LocalNotificationSchedulerImpl(val context: Context) : LocalNotificationScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(notificationItem: NotificationItem) {
        val time = notificationItem.time
        val intent = Intent(context, LocalNotificationReceiver::class.java).apply {
            putExtra(
                EXTRA_NOTIFICATION_TYPE,
                notificationItem.notificationType.name
            )
            putExtra(EXTRA_NOTIFICATION_TIME, time)
            putExtra(EXTRA_REQUEST_CODE, notificationItem.id)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            PendingIntent.getBroadcast(
                context,
                time.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancel(id: Int) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                id, // use same id that is used to schedule the alarm to cancel it
                Intent(context, LocalNotificationReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

}