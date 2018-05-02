package com.sunilson.firenote.presentation.shared.singletons

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityManager @Inject constructor() {
    fun isConnected() : Boolean {
        return false
    }
}