package com.vocable.data.di

import androidx.activity.ComponentActivity
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sync.networkModule
import com.vocable.data.auth.domain.repository.AuthRepository
import com.vocable.data.auth.domain.repository.AuthRepositoryImpl
import com.vocable.data.auth.source.remote.FirebaseAuthSource
import com.vocable.data.auth.source.remote.GoogleAuthProvider
import com.vocable.data.db.VocableDatabase
import com.vocable.data.quiz.domain.repository.QuizRepository
import com.vocable.data.quiz.domain.repository.QuizRepositoryImpl
import com.vocable.data.user.domain.repository.UserRepository
import com.vocable.data.user.domain.repository.UserRepositoryImpl
import com.vocable.data.user.source.locale.LocalUserSource
import com.vocable.data.user.source.remote.FirebaseUserSource
import com.vocable.data.word.domain.repository.WordsRepository
import com.vocable.data.word.domain.repository.WordsRepositoryImpl
import com.vocable.data.word.source.remote.FirebaseDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val dataModules = listOf(
    module {
        single { FirebaseApp.initializeApp(androidContext()) }
        single { Firebase.auth }
        single { Firebase.firestore }
        single {
            Room.databaseBuilder(androidContext(), VocableDatabase::class.java, "vocable.db")
                .fallbackToDestructiveMigration()
                .build()
        }
        factory { (activity: ComponentActivity) -> GoogleAuthProvider(activity) }
        single { get<VocableDatabase>().wordDao() }
        single { FirebaseDataSource(get()) }

        single<WordsRepository> { WordsRepositoryImpl(get(), get(), get(),get()) }
        single { FirebaseAuthSource(get()) }
        single { FirebaseUserSource(get()) }
        single { LocalUserSource(androidContext()) }
        single<AuthRepository> { AuthRepositoryImpl(get()) }
        single<UserRepository> { UserRepositoryImpl(get(), get()) }
        single<QuizRepository> { QuizRepositoryImpl() }


    }, networkModule
)