package com.pluto.downloadmanager

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy
import com.google.android.material.snackbar.Snackbar
import com.pluto.downloadmanager.download.Downloader
import com.pluto.downloadmanager.download.core.OnDownloadListener
import com.pluto.downloadmanager.utils.HashUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    private var downloader: Downloader? = null
    private val TAG: String = this::class.java.name

    companion object {
        private var isStarted: Boolean=false
    }


    private val handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getDownloader()

        //network observers
        val settings = InternetObservingSettings.builder()
            .host("www.google.com")
            .strategy(SocketInternetObservingStrategy())
            .build()

        ReactiveNetwork
            .observeInternetConnectivity(settings)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToInternet: Boolean->
                if (!isConnectedToInternet) {
                    downloader?.pauseDownload()
                    Snackbar.make(root_view, "Net NOT Connected", Snackbar.LENGTH_SHORT).show()
                }
               else if(isStarted){
                    getDownloader()
                    downloader?.resumeDownload()
                    Snackbar.make(root_view, "Net Connected ,Starting download it might take a while with depending on your internet speed." , Snackbar.LENGTH_SHORT).show()
                }
            }

/*        NetworkObs(this@MainActivity).observe(this) {
            when (it) {
                NetworkStatus.Available -> {
                    Snackbar.make(root_view, "Net Connected", Snackbar.LENGTH_SHORT).show()
                    getDownloader()
                    downloader?.resumeDownload()
                }
                NetworkStatus.Unavailable -> {
                    Log.d("alexnet","unaviable");
                    Snackbar.make(root_view, "Net Disconnected", Snackbar.LENGTH_SHORT).show()
                    downloader?.pauseDownload()
                }
            }
        }*/
        start_download_btn.setOnClickListener {
            isStarted=true
            getDownloader()
            downloader?.download()
            checksum_txt.text="";
        }
        restart_download_btn.setOnClickListener {
            getDownloader()
            downloader?.restartDownload()
            checksum_txt.text="";
        }
        pause_download_btn.setOnClickListener {
            downloader?.pauseDownload()
            checksum_txt.text="";
        }
        resume_download_btn.setOnClickListener {
            getDownloader()
            downloader?.resumeDownload()
            checksum_txt.text="";
        }
    }

    private fun getDownloader() {
        downloader = Downloader.Builder(
            this,
            "https://speed.hetzner.de/100MB.bin"
        ).downloadListener(object : OnDownloadListener {
            override fun onStart() {
                handler.post { current_status_txt.text = "onStart" }
                Log.d(TAG, "onStart")
            }

            override fun onPause() {
                handler.post { current_status_txt.text = "onPause" }
                Log.d(TAG, "onPause")
            }

            override fun onResume() {
                handler.post { current_status_txt.text = "onResume" }
                Log.d(TAG, "onResume")
            }

            override fun onProgressUpdate(percent: Int, downloadedSize: Int, totalSize: Int) {
                handler.post {
                    current_status_txt.text = "onProgressUpdate"
                    percent_txt.text = percent.toString().plus("%")
                    size_txt.text = getSize(downloadedSize)
                    total_size_txt.text = getSize(totalSize)
                    download_progress.progress = percent
                }
                Log.d(
                    TAG,
                    "onProgressUpdate: percent --> $percent downloadedSize --> $downloadedSize totalSize --> $totalSize "
                )
            }

            override fun onCompleted(file: File?) {
                handler.post { current_status_txt.text = "onCompleted" }
                val checkSumSha256 = HashUtils.getCheckSumFromFile(
                    MessageDigest.getInstance("SHA-256"),
                    file
                )
                checksum_txt.text="CheckSum  $checkSumSha256"
                Log.d(TAG, "onCompleted: file --> $checkSumSha256")
            }

            override fun onFailure(reason: String?) {
                handler.post { current_status_txt.text = "onFailure: reason --> $reason" }
                Log.d(TAG, "onFailure: reason --> $reason")
            }

            override fun onCancel() {
                handler.post { current_status_txt.text = "onCancel" }
                Log.d(TAG, "onCancel")
            }
        }).build()
    }

    fun getSize(size: Int): String {
        var s = ""
        val kb = (size / 1024).toDouble()
        val mb = kb / 1024
        val gb = kb / 1024
        val tb = kb / 1024
        if (size < 1024) {
            s = "$size Bytes"
        } else if (size >= 1024 && size < 1024 * 1024) {
            s = String.format("%.2f", kb) + " KB"
        } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
            s = String.format("%.2f", mb) + " MB"
        } else if (size >= 1024 * 1024 * 1024 && size < 1024 * 1024 * 1024 * 1024) {
            s = String.format("%.2f", gb) + " GB"
        } else if (size >= 1024 * 1024 * 1024 * 1024) {
            s = String.format("%.2f", tb) + " TB"
        }
        return s
    }
}