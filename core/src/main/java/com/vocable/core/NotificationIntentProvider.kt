package com.vocable.core

import android.content.Intent

interface NotificationIntentProvider {
    fun provideIntent(): Intent
}