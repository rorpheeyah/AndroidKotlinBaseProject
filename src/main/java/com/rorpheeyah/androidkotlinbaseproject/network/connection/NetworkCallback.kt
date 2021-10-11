package com.rorpheeyah.androidkotlinbaseproject.network.connection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build

/*
     _   _      _                      _     ____      _ _ _                _
    | \ | | ___| |___      _____  _ __| | __/ ___|__ _| | | |__   __ _  ___| | __
    |  \| |/ _ \ __\ \ /\ / / _ \| '__| |/ / |   / _` | | | '_ \ / _` |/ __| |/ /
    | |\  |  __/ |_ \ V  V / (_) | |  |   <| |__| (_| | | | |_) | (_| | (__|   <
    |_| \_|\___|\__| \_/\_/ \___/|_|  |_|\_\\____\__,_|_|_|_.__/ \__,_|\___|_|\_\

 */
/**
 * @author Matt Dev
 * @since 2021.02.05
 */
open class NetworkCallback(context: Context) : ConnectivityManager.NetworkCallback() {
    private var connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var networkObserve: NetworkObservation

    /**
     * uregister the network callback
     */
    fun registerNetworkCallback(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // register the default network callback for N or newer
            connectivityManager.registerDefaultNetworkCallback(this)
        }else{
            // register the default network callback for lower version of N
            connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), ConnectivityManager.NetworkCallback())
        }
    }

    /**
     * unregister the network callback
     */
    fun unregisterNetworkCallback(){
        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), this)
    }

    /**
     * Observe network connection
     */
    fun observeNetworkConnection(network: NetworkObservation){
        networkObserve = network
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)

        networkObserve.isActive(true)
    }

    override fun onLost(network: Network) {
        super.onLost(network)

        networkObserve.isActive(false)
    }

    interface NetworkObservation{
        fun isActive(isOnline : Boolean)
    }
}