package com.example.bloom.network

import android.Manifest
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

//Used Medium for connectivity Observer
// https://medium.com/@KaushalVasava/how-observe-internet-in-android-a-new-way-using-flow-8304a33b4717
class ConnectivityObserver(context: Context) {

    //Initialising connectivity manager using getSystemService
    private var connectivityManager: ConnectivityManager? =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    //Creating a network callback
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    val isConnectedFlow: Flow<Boolean>  = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                connectivityManager?.getNetworkCapabilities(network)?.let {
                    if (it.hasCapability(NET_CAPABILITY_INTERNET)) {
                        trySend(true)
                    }
                }
            }

            //Called when a network is lost
            override fun onLost(network: Network) {
                trySend(false)
            }

            //Called when network is unavailable
            override fun onUnavailable() {
                trySend(false)
            }

            //Called when capabilities of the network changes
            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, capabilities)
                if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                    trySend(true)
                } else {
                    trySend(false)
                }
            }
        }

        //Creating a network Request
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        //Registering the network callback
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)

        awaitClose {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged()
}