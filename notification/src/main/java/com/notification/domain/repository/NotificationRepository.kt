package com.notification.domain.repository

import android.content.Intent
import com.notification.domain.model.NotificationType

interface NotificationRepository {

    fun scheduleNotifications(items: List<Long>, type: NotificationType)
    fun scheduleNotification(item: Long, type: NotificationType)

    fun updateNotificationTime(item: Long, type: NotificationType)

    fun cancelNotification(id: Int)

    fun showNotification(
        notificationType: NotificationType,
        notificationId: Int,
        contentIntent: Intent
    )
}