package com.pluto.downloadmanager.download

/**
 * Author:  upendra 
 * Date:    21/6/2019
 * Email:   example@mail.com
 */

internal interface IDownload {
    fun download()
    fun cancelDownload()
    fun pauseDownload()
    fun resumeDownload()
    fun  restartDownload()
}