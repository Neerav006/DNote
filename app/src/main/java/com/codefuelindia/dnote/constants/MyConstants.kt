package com.codefuelindia.dnote.constants

import android.content.Context
import android.view.View
import android.widget.Toast
import android.view.inputmethod.InputMethodManager
import android.net.ConnectivityManager




class MyConstants {

    companion object {

        val BASE_URL = "http://www.dnote.xyz/api/"
            const val WRITE_EXTERNAL_STORAGE:Int = 100



        fun showToast(contex: Context, msg: String) {

            Toast.makeText(contex, msg, Toast.LENGTH_LONG).show()
        }

        fun hideSoftKeyBoard(view: View?, contex: Context) {

            if (view != null) {
                val imm = contex.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }


        fun checkInternetConnection(context: Context): Boolean {

            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }

    }


}