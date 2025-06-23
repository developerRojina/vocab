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
import com.vocable.notification.domain.model.NotificationContent
import com.vocable.notification.domain.model.NotificationType
import com.vocable.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext
import timber.log.Timber

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

        Timber.d("inside on receive of LocalNotificationReceiver")

        val notificationTypeName = intent.getStringExtra(EXTRA_NOTIFICATION_TYPE) ?: return
        val notificationType = try {
            NotificationType.valueOf(notificationTypeName)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            return
        }
        val id = intent.getIntExtra(EXTRA_REQUEST_CODE, 0)
        val time = intent.getLongExtra(EXTRA_NOTIFICATION_TIME, 0)


        var notificationContent: String? = null
        var notificationTitle: String? = null
        CoroutineScope(Dispatchers.IO).launch {
            if (notificationType == NotificationType.NEW_WORDS) {
                Timber.d("inside on receive of LocalNotificationReceiver for new words")
                updateWords()
                notificationContent = "Your new words are ready!! Read them up :) :)"
                notificationTitle = getWords().joinToString(",")

            } else if (notificationType == NotificationType.WORD_REMINDER) {
                notificationContent = "Remember these words. They are the words for today :) :)"
                notificationTitle = getWords().joinToString(",")

            }

            Timber.d("the notification content is $notificationContent")

            val contentIntent = Intent(context, MainActivity::class.java)
            contentIntent.putExtra(EXTRA_NOTIFICATION_TYPE, notificationType.name)
            val content = NotificationContent(
                notificationId = id,
                contentIntent = contentIntent,
                notificationType = notificationType,
                notificationTitle = notificationTitle,
                notificationContent = notificationContent
            )

            notificationRepository.showNotification(content)
            notificationRepository.scheduleNotification(
                type = notificationType,
                item = TimeAndDateUtils.addDayToTimestamp(time)
            )
        }
    }


    suspend fun getWords(): List<String> {
        val user = userRepository.getMyDetail().first()
        return user?.let {
            val wordIds = it.vocabStats.currentWords
            val words = wordIds.mapNotNull { wordsRepository.getWordDetail(it) }
            words.map { it.word }

        } ?: emptyList()

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