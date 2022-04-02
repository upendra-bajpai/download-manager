package com.pluto.downloadmanager.download

import com.pluto.downloadmanager.download.core.OnDownloadListener
import com.pluto.downloadmanager.download.core.database.DownloaderDatabase
import com.pluto.downloadmanager.download.helper.ConnectionHelper
import android.Manifest
import android.content.Context
import androidx.annotation.CheckResult
import androidx.annotation.RequiresPermission
import com.pluto.downloadmanager.download.core.DownloadTaskManager
import kotlinx.coroutines.GlobalScope
import java.lang.ref.WeakReference
import java.net.MalformedURLException

/**
 * Author:  upendra 
 * Date:    21/6/2019
 * Email:   example@mail.com
 */

class Downloader private constructor(downloadTask: DownloadTaskManager) : IDownload {

    //region field
    private var mDownloadTask: DownloadTaskManager? = null
    //endregion

    //region initialize
    init {
        if (mDownloadTask == null)
            mDownloadTask = downloadTask
    }
    //endregion

    //region method interface
    @RequiresPermission(Manifest.permission.INTERNET)
    override fun download() {
        if (mDownloadTask == null)
            throw IllegalAccessException("Rebuild new instance after \"pause or cancel\" download")
        mDownloadTask?.executeOnExecutor()
    }

    override fun restartDownload() {
        mDownloadTask?.resume=false
        mDownloadTask?.restart = true
        download()
    }

    override fun cancelDownload() {
        mDownloadTask?.cancel()
        mDownloadTask = null
    }

    override fun pauseDownload() {
        mDownloadTask?.pause()
        mDownloadTask = null
    }

    override fun resumeDownload() {
        mDownloadTask?.resume = true
        download()
    }

    //endregion

    class Builder(private val mContext: Context, private var mUrl: String) {

        //region field
        private var mTimeOut: Int = 0
        private var mDownloadDir: String? = null
        private var mFileName: String? = null
        private var mExtension: String? = null
        private var mDownloadListener: OnDownloadListener? = null
        private var mHeader: Map<String, String>? = null
        //endregion

        /**
         * @param downloadDir for setting custom download directory (default value is sandbox/download/ directory)
         * @return builder
         */
        @CheckResult
        fun downloadDirectory(downloadDir: String): Builder {
            this.mDownloadDir = downloadDir
            return this
        }

        /**
         * @param downloadListener an event listener for tracking download events
         * @return builder
         */
        @CheckResult
        fun downloadListener(downloadListener: OnDownloadListener): Builder {
            this.mDownloadListener = downloadListener
            return this
        }

        /**
         * @param downloadListener remove listener
         * @return builder
         */
        @CheckResult
        fun removeDownloadListener(): Builder {
            this.mDownloadListener = null
            return this
        }

        /**
         * @param fileName  for saving with this name
         * @param extension extension of the file
         * @return builder
         */
        @CheckResult
        fun fileName(fileName: String, extension: String): Builder {
            this.mFileName = fileName
            this.mExtension = extension
            return this
        }

        /**
         * @param header for adding headers in http request
         * @return builder
         */
        @CheckResult
        fun header(header: Map<String, String>): Builder {
            this.mHeader = header
            return this
        }

        /**
         * @param timeOut is a parameter for setting connection time out.
         * @return Builder
         */
        @CheckResult
        fun timeOut(timeOut: Int): Builder {
            this.mTimeOut = timeOut
            return this
        }

        fun build(): Downloader {
            mUrl =
                if (mUrl.isEmpty()) throw MalformedURLException("The entered URL is not valid")
                else mUrl

            mDownloadDir =
                if (mDownloadDir == null || mDownloadDir!!.isEmpty()) mContext.getExternalFilesDir(null)?.toString()
                else mDownloadDir

            mTimeOut =
                if (mTimeOut == 0) ConnectionHelper.TIME_OUT_CONNECTION
                else mTimeOut

            val downloadTask = DownloadTaskManager(
                mUrl,
                WeakReference(mContext),
                DownloaderDatabase.getAppDatabase(mContext).downloaderDao(),
                mDownloadDir,
                mTimeOut,
                mDownloadListener,
                mHeader,
                mFileName,
                mExtension
            )
            return Downloader(downloadTask)
        }
    }
}
