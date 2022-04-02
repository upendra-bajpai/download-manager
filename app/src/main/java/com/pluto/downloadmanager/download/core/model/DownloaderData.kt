package com.pluto.downloadmanager.download.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author:  upendra 
 * Date:    21/6/2019
 * Email:   example@mail.com
 */

@Entity
data class DownloaderData(
    @PrimaryKey val id: Int,
    val url: String?,
    val filename: String?,
    val status: Int,
    val percent: Int,
    val size: Int,
    val totalSize: Int,
    val fullName:String="nan"
)