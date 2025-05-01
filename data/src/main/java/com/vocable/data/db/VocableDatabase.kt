package com.vocable.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vocable.data.word.domain.model.WordEntity
import com.vocable.data.word.source.local.WordsDao

@TypeConverters(DbTypeConverter::class)
@Database(entities = [WordEntity::class], version = 1, exportSchema = false)
abstract class VocableDatabase : RoomDatabase() {
    abstract fun wordDao(): WordsDao
}