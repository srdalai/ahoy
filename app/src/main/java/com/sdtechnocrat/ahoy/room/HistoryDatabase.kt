package com.sdtechnocrat.ahoy.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sdtechnocrat.ahoy.data.HistoryItem

@Database(entities = [HistoryItem::class], version = 5)
abstract class HistoryDatabase : RoomDatabase() {
    companion object {
        private const val DATA_BASE_NAME = "search-history"
        private var instance: HistoryDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): HistoryDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(ctx.applicationContext, HistoryDatabase::class.java, DATA_BASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }

    abstract fun historyDao() : HistoryDao
}