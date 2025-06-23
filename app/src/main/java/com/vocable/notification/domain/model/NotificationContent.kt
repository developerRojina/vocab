package com.vocable.notification.domain.model

import android.content.Intent

data class NotificationContent(
    val notificationType: NotificationType,
    val notificationId: Int,
    val notificationContent: String?,
    val notificationTitle: String?,
    val contentIntent: Intent
)