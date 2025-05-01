package com.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TimeAndDateUtils {


    fun getDefaultQuizTime(): Long {
        // 8:00 PM (24-hour format)
        return getTimeStampFormHour(20, 0)
    }

    fun getDefaultNewWordsNotificationTime(): Long {
        // 7:00 AM (24-hour format)
        return getTimeStampFormHour(7, 0)
    }

    fun getDefaultWordsReminderTime(): List<Long> {
        // 11:00 AM (24-hour format)
        return listOf<Long>(
            getTimeStampFormHour(11, 0),
            getTimeStampFormHour(15, 0),
            getTimeStampFormHour(20, 0),
        )
    }


    fun getCurrentTimeStampEpocMillis(): Long {
        return Instant.now().toEpochMilli()
    }

    fun getTimeStampFormHour(hour: Int, minute: Int): Long {
        val today = LocalDate.now()
        val time = LocalTime.of(hour, minute)
        val dateTime = LocalDateTime.of(today, time)
        val zoneId = ZoneId.systemDefault()
        val instant = dateTime.atZone(zoneId).toInstant()
        return instant.toEpochMilli()

    }

    fun hasTimePassed(time: Long): Boolean {
        val zoneId = ZoneId.systemDefault()
        val instant = Instant.ofEpochMilli(time)
        val zonedDateTime = instant.atZone(zoneId)

        val currentTime = ZonedDateTime.now(zoneId)
        return zonedDateTime.isBefore(currentTime)
    }

    fun addDayToTimestamp(originalTimestamp: Long): Long {
        val zoneId = ZoneId.systemDefault()
        val instant = Instant.ofEpochMilli(originalTimestamp)
        val zonedDateTime = instant.atZone(zoneId)
        val updatedZonedDateTime = zonedDateTime.plusDays(1)
        return updatedZonedDateTime.toInstant().toEpochMilli()
    }

    fun convertUtcToLocalTime(utcMillis: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault() // Convert to user's local time zone
        return sdf.format(Date(utcMillis))
    }
}