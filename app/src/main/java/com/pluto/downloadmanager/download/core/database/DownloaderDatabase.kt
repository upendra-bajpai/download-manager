package com.pluto.downloadmanager.download.core.database

import com.pluto.downloadmanager.download.core.model.DownloaderData
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Author:  upendra 
 * Date:    21/6/2019
 * Email:   example@mail.com
 */
@Database(entities = [DownloaderData::class], version = 12, exportSchema = false)
internal abstract class DownloaderDatabase : RoomDatabase() {

    abstract fun downloaderDao(): DownloaderDao

    companion object {

        private var INSTANCE: DownloaderDatabase? = null

        fun getAppDatabase(context: Context): DownloaderDatabase =
            INSTANCE?.let { it }
                ?: Room.databaseBuilder(
                    context.applicationContext,
                    DownloaderDatabase::class.java,
                    "downloader_db"
                ) .fallbackToDestructiveMigration().build().apply { INSTANCE = this }
    }
}
