package com.pluto.downloadmanager.download

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class NetworkObs(context: Context) : LiveData<NetworkStatus>() {

        val valideNetworkConnections : ArrayList<Network> = ArrayList()
        var connectivityManager: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        private lateinit var connectivityManagerCallback: ConnectivityManager.NetworkCallback

        fun announceStatus(){
            if (valideNetworkConnections.isNotEmpty()){
                postValue(NetworkStatus.Available)
            } else {
                postValue(NetworkStatus.Unavailable)
            }
        }

        fun getConnectivityManagerCallback() =
            object : ConnectivityManager.NetworkCallback(){
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    val networkCapability = connectivityManager.getNetworkCapabilities(network)
                    val hasNetworkConnection = networkCapability?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)?:false
                    if (hasNetworkConnection){
                        determineInternetAccess(network)
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    valideNetworkConnections.remove(network)
                    announceStatus()
                }

                override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){
                        determineInternetAccess(network)
                    } else {
                        valideNetworkConnections.remove(network)
                    }
                    announceStatus()
                }
            }

        private fun determineInternetAccess(network: Network) {
            CoroutineScope(Dispatchers.IO).launch{
                if (InernetAvailablity.check()){
                    withContext(Dispatchers.Main){
                        valideNetworkConnections.add(network)
                        announceStatus()
                    }
                }
            }
        }


        override fun onActive() {
            super.onActive()
            connectivityManagerCallback = getConnectivityManagerCallback()
            val networkRequest = NetworkRequest
                .Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(networkRequest, connectivityManagerCallback)
        }

        override fun onInactive() {
            super.onInactive()
            connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
        }

    }

    object InernetAvailablity {

        fun check() : Boolean {
            return try {
                val socket = Socket()
                socket.connect(InetSocketAddress("8.8.8.8",53))
                socket.close()
                true
            } catch ( e: Exception){
                e.printStackTrace()
                false
            }
        }

    }

sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
}