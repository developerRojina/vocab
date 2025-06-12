package com.vocable

import android.app.Application
import com.vocable.data.di.dataModules
import com.vocable.di.appModules
import com.vocable.notification.notificationModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class VocableApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@VocableApp)
            modules(appModules, notificationModules, *dataModules.toTypedArray())
        }
    }
}