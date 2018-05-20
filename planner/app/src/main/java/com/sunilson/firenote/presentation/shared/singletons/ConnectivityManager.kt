package com.sunilson.firenote.presentation.shared.singletons

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton
import android.net.NetworkInfo
import android.net.ConnectivityManager

@Singleton
class ConnectivityManager @Inject constructor(val context: Context) {
    fun isConnected() : Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}