package com.vocable.di

import com.vocable.MainActivityIntentProviderImpl
import com.vocable.MainViewModel
import com.vocable.auth.LoginViewModel
import com.vocable.core.NotificationIntentProvider
import com.vocable.dashboard.DashboardViewModel
import com.vocable.home.HomeViewModel
import com.vocable.profile.ProfileViewModel
import com.vocable.quiz.QuizViewModel
import com.vocable.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { LoginViewModel(get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { QuizViewModel(get(), get(), get()) }

    single<NotificationIntentProvider> { MainActivityIntentProviderImpl(androidContext()) }

}