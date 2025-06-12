package com.vocable.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.utils.TimeAndDateUtils
import com.vocable.MainActivity
import com.vocable.data.user.domain.repository.UserRepository
import com.vocable.data.word.domain.repository.WordsRepository
import com.vocable.notification.Constants.EXTRA_NOTIFICATION_TIME
import com.vocable.notification.Constants.EXTRA_NOTIFICATION_TYPE
import com.vocable.notification.Constants.EXTRA_REQUEST_CODE
import com.vocable.notification.domain.model.NotificationType
import com.vocable.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext

/**
 * Receiver for handling notification alarms
 */
class LocalNotificationReceiver() : BroadcastReceiver() {


    lateinit var notificationRepository: NotificationRepository
    lateinit var userRepository: UserRepository
    lateinit var wordsRepository: WordsRepository


    override fun onReceive(context: Context, intent: Intent) {
        val koin = GlobalContext.get()

        if (!::notificationRepository.isInitialized) {
            notificationRepository = koin.get()
            userRepository = koin.get()
            wordsRepository = koin.get()

        }
        GlobalScope.launch(Dispatchers.IO) {
            updateWords()
        }

        val notificationTypeName = intent.getStringExtra(EXTRA_NOTIFICATION_TYPE) ?: return
        val notificationType = try {
            NotificationType.valueOf(notificationTypeName)
        } catch (e: IllegalArgumentException) {
            return
        }
        val id = intent.getIntExtra(EXTRA_REQUEST_CODE, 0)
        val time = intent.getLongExtra(EXTRA_NOTIFICATION_TIME, 0)


        val contentIntent = Intent(context, MainActivity::class.java)
        contentIntent.putExtra(EXTRA_NOTIFICATION_TYPE, notificationType.name)

        notificationRepository.showNotification(notificationType, id, contentIntent)

        notificationRepository.scheduleNotification(
            type = notificationType,
            item = TimeAndDateUtils.addDayToTimestamp(time)
        )


    }

    suspend fun updateWords() {
        withContext(Dispatchers.IO) {
            val userDetail = userRepository.getMyDetail().first()
            userDetail?.let {
                val quota = userDetail.preference.dailyWordQuota
                val existingWords = userDetail.vocabStats.currentWords

                wordsRepository.updateWordStatusToLearned(existingWords)

                val words = wordsRepository.getWordsOfTheDay(quota).first()

                wordsRepository.updateWordStatusToAssigned(words)
                val indexes = existingWords.map { wordId ->
                    async {
                        wordsRepository.getWordDetail(wordId)?.index
                    }
                }.awaitAll().filterNotNull()
                userRepository.updateLearnedWords(userDetail.vocabStats.currentWords, indexes)

                userRepository.updateCurrentWords(words)
            }
        }

    }


}