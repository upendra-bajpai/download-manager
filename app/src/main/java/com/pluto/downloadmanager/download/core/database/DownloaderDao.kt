package com.pluto.downloadmanager.download.core.database

import com.pluto.downloadmanager.download.core.model.DownloaderData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Author:  upendra 
 * Date:    21/6/2019
 * Email:   example@mail.com
 */
@Dao
interface   DownloaderDao {

    @Query("SELECT * FROM DownloaderData WHERE url IS :url")
    fun getDownloadByUrl(url: String): DownloaderData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewDownload(vararg item: DownloaderData)

    @Query("UPDATE DownloaderData SET status= :success, percent=:percent, size=:downloadedSize, totalSize=:totalSize ,fullName=:fullName WHERE url IS :url")
    fun updateDownload(url: String, success: Int, percent: Int, downloadedSize: Int, totalSize: Int,fullName:String="")
}