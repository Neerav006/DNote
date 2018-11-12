package com.codefuelindia.dnote

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class MyApp :Application() {

    override fun onCreate() {
        super.onCreate()




    }

    fun isNetAvailable(context: Context):Boolean{

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true


    }


}