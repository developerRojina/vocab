package com.vocable

import android.content.Context
import android.content.Intent
import com.vocable.core.NotificationIntentProvider

class MainActivityIntentProviderImpl(val context: Context) : NotificationIntentProvider {
    override fun provideIntent(): Intent {
        return Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
}