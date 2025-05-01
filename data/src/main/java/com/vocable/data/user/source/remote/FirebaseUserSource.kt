package com.vocable.data.user.source.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.utils.TimeAndDateUtils
import com.utils.UserKeyUtils.KEY_SETTINGS_DAILY_WORDS_QUOTA_COUNT
import com.utils.UserKeyUtils.KEY_SETTINGS_DISPLAY_THEME
import com.utils.UserKeyUtils.KEY_SETTINGS_NEW_WORDS_NOTIFICATION_TIME
import com.utils.UserKeyUtils.KEY_SETTINGS_QUIZ_NOTIFICATION_TIME
import com.utils.UserKeyUtils.KEY_SETTINGS_WORDS_REMINDER_NOTIFICATION_TIME
import com.utils.UserKeyUtils.KEY_USER_ACTIVE_WORDS
import com.utils.UserKeyUtils.KEY_USER_LEARNED_WORDS
import com.utils.UserKeyUtils.KEY_USER_LOGGED_IN_TIME
import com.utils.UserKeyUtils.KEY_USER_QUIZZED_WORDS
import com.vocable.data.FirestoreCollections
import com.vocable.data.result.AppResult
import com.vocable.data.result.safeCall
import com.vocable.data.user.domain.model.AppUserDetail
import com.vocable.data.user.mapper.SelectedTheme
import com.vocable.data.user.mapper.toFirebaseRemoteData
import com.vocable.data.user.mapper.toUserDetail
import kotlinx.coroutines.tasks.await


class FirebaseUserSource(private val store: FirebaseFirestore) {


    suspend fun addUserInDb(userDetail: AppUserDetail): AppResult<Unit> = safeCall {
        val userRef = store.collection(FirestoreCollections.users)
            .document(userDetail.id)
        store.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            if (snapshot.exists()) {
                transaction.update(
                    userRef,
                    KEY_USER_LOGGED_IN_TIME,
                    TimeAndDateUtils.getCurrentTimeStampEpocMillis()
                )
            } else {
                transaction.set(userRef, userDetail.toFirebaseRemoteData())
            }
        }
    }


    suspend fun getUserDetail(id: String): AppUserDetail? {
        val userRef = store.collection(FirestoreCollections.users)
            .document(id).get().await()
        return userRef.data?.toUserDetail()

    }

    suspend fun updateActiveWords(
        userId: String,
        words: List<String>
    ): AppResult<Unit> = safeCall {
        safeCall {
            val userRef = store.collection(FirestoreCollections.users)
                .document(userId)
            userRef.update(KEY_USER_ACTIVE_WORDS, words).await()
        }
    }

    suspend fun updateQuizzedWords(
        userId: String,
        words: List<String>
    ): AppResult<Unit> = safeCall {
        val userRef = store.collection(FirestoreCollections.users)
            .document(userId)
        userRef.update(KEY_USER_QUIZZED_WORDS, words).await()
    }

    suspend fun updateLearnedWords(
        userId: String,
        words: List<String>
    ): AppResult<Unit> = safeCall {
        val userRef = store.collection(FirestoreCollections.users)
            .document(userId)
        userRef.update(KEY_USER_LEARNED_WORDS, FieldValue.arrayUnion(*words.toTypedArray())).await()
    }

    suspend fun updateNewWordsNotificationTime(
        userId: String,
        time: Long
    ): AppResult<Unit> = safeCall {
        val userRef = store.collection(FirestoreCollections.users)
            .document(userId)
        userRef.update(KEY_SETTINGS_NEW_WORDS_NOTIFICATION_TIME, time).await()
    }

    suspend fun updateWordReminderTimes(
        userId: String,
        notificationTimes: List<Long>
    ): AppResult<Unit> = safeCall {
        val userRef = store.collection(FirestoreCollections.users)
            .document(userId)

        userRef.update(KEY_SETTINGS_WORDS_REMINDER_NOTIFICATION_TIME, notificationTimes)
            .await()
    }

    suspend fun updateNewQuizNotificationTime(
        userId: String,
        notificationTimes: List<Long>
    ): AppResult<Unit> = safeCall {
        val userRef = store.collection(FirestoreCollections.users)
            .document(userId)
        userRef.update(KEY_SETTINGS_QUIZ_NOTIFICATION_TIME, notificationTimes).await()
    }

    suspend fun updateDailyQuotaNotificationCount(
        userId: String,
        quotaCount: Int
    ): AppResult<Unit> = safeCall {
        val userRef = store.collection(FirestoreCollections.users)
            .document(userId)
        userRef.update(KEY_SETTINGS_DAILY_WORDS_QUOTA_COUNT, quotaCount).await()
    }

    suspend fun updateAppTheme(
        userId: String,
        selectedTheme: SelectedTheme
    ): AppResult<Unit> = safeCall {
        val userRef = store.collection(FirestoreCollections.users)
            .document(userId)
        userRef.update(KEY_SETTINGS_DISPLAY_THEME, selectedTheme.name).await()
    }

}