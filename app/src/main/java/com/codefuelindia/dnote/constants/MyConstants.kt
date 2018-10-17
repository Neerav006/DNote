package com.codefuelindia.dnote.constants

import android.content.Context
import android.view.View
import android.widget.Toast
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager


class MyConstants {

    companion object {

        val BASE_URL = "http://code-fuel.in/dnote/"


        fun showToast(contex: Context, msg: String) {

            Toast.makeText(contex, msg, Toast.LENGTH_LONG).show()
        }

        fun hideSoftKeyBoard(view: View?, contex: Context) {

            if (view != null) {
                val imm = contex.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

    }


}