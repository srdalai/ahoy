package com.sdtechnocrat.ahoy.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sdtechnocrat.ahoy.data.DownloadItem

@Database(entities = [DownloadItem::class], version = 5)
abstract class DownloadDatabase: RoomDatabase() {
    companion object {
        private const val DATA_BASE_NAME = "downloads"
        private var instance: DownloadDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): DownloadDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(ctx.applicationContext, DownloadDatabase::class.java, DATA_BASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }

    abstract fun downloadDao() : DownloadDao
}