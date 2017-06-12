package com.example.popularmovies.utilities

import android.content.Context
import android.net.ConnectivityManager



/**
 * Utility class
 * Created by carlos on 6/8/17.
 */
object Utility {
    fun hasInternetConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}