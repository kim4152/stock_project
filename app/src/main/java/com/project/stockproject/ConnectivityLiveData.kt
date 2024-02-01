package com.project.stockproject

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData

class ConnectivityLiveData(private val context: Context) : LiveData<Boolean>() {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback: ConnectivityManager.NetworkCallback? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createNetworkCallback()
        } else {
            null
        }

    private val broadcastReceiver: BroadcastReceiver? =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            createBroadcastReceiver()
        } else {
            null
        }

    override fun onActive() {
        super.onActive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCallback?.let {
                connectivityManager.registerDefaultNetworkCallback(it)
            }
        } else {
            broadcastReceiver?.let {
                val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                context.registerReceiver(it, intentFilter)
            }
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCallback?.let {
                connectivityManager.unregisterNetworkCallback(it)
            }
        } else {
            broadcastReceiver?.let {
                context.unregisterReceiver(it)
            }
        }
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            val isInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            postValue(isInternet && isValidated)
        }

        override fun onLost(network: Network) {
            postValue(false)
        }
    }

    private fun createBroadcastReceiver() = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isNoConnectivity = intent?.extras?.getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY) ?: true
            postValue(!isNoConnectivity)
        }
    }
}