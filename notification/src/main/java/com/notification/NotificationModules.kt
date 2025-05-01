package com.notification

import com.notification.domain.repository.NotificationRepository
import com.notification.domain.repository.NotificationRepositoryImpl
import com.notification.scheduler.LocalNotificationScheduler
import com.notification.scheduler.LocalNotificationSchedulerImpl
import com.vocable.core.NotificationIntentProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val notificationModules = module {
    single<NotificationRepository> { NotificationRepositoryImpl(androidContext(), get(), get()) }
    single { NotificationChannelManager() }
    factory {
        val provider: NotificationIntentProvider = get()
        provider.provideIntent()
    }
    single<LocalNotificationScheduler> { LocalNotificationSchedulerImpl(get()) }
}