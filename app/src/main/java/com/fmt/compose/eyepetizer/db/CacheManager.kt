package com.fmt.compose.eyepetizer.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fmt.compose.eyepetizer.mainApplication

@Database(entities = [Video::class], version = 1)
abstract class CacheManager : RoomDatabase() {
    abstract val videoDao: VideoDao

    companion object {
        private val database =
            Room.databaseBuilder(mainApplication, CacheManager::class.java, "compose_cache")
                .build()

        @JvmStatic
        fun get(): CacheManager {
            return database
        }
    }
}