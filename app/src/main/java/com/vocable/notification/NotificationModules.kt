package com.vocable.notification

import com.vocable.notification.domain.repository.NotificationRepository
import com.vocable.notification.domain.repository.NotificationRepositoryImpl
import com.vocable.notification.scheduler.LocalNotificationScheduler
import com.vocable.notification.scheduler.LocalNotificationSchedulerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val notificationModules = module {
    single<NotificationRepository> { NotificationRepositoryImpl(androidContext(), get(), get()) }
    single { NotificationChannelManager() }

    single<LocalNotificationScheduler> { LocalNotificationSchedulerImpl(get()) }
}