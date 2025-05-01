package com.notification.scheduler

import com.notification.domain.model.NotificationItem

interface LocalNotificationScheduler {
    fun schedule(notificationItem: NotificationItem)
    fun cancel(id: Int)
}