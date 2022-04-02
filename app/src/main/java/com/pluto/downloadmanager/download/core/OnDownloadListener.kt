package com.pluto.downloadmanager.download.core

import java.io.File

/**
 * Author:  upendra 
 * Date:    21/6/2019
 * Email:   example@mail.com
 */

interface OnDownloadListener {
    fun onStart()
    fun onPause()
    fun onResume()
    fun onProgressUpdate(percent: Int, downloadedSize: Int, totalSize: Int)
    fun onCompleted(file: File?)
    fun onFailure(reason: String?)
    fun onCancel()
}
