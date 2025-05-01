package com.notification.domain.model

data class NotificationItem(
    val time: Long,
    val id: Int,
    val notificationType: NotificationType,
)

enum class NotificationType {
    NEW_WORDS,
    QUIZ_REMINDER,
    WORD_REMINDER
}