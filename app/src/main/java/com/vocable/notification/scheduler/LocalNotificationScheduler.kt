package com.vocable.notification.scheduler

import com.vocable.notification.domain.model.NotificationItem

interface LocalNotificationScheduler {
    fun schedule(notificationItem: NotificationItem)
    fun cancel(id: Int)
}