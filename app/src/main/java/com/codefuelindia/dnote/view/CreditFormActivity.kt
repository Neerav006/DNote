package com.codefuelindia.dnote.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import com.codefuelindia.dnote.R

import kotlinx.android.synthetic.main.activity_credit_form.*
import kotlinx.android.synthetic.main.content_credit_form.*
import android.view.MotionEvent



class CreditFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_form)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.title = "Credit form"
        toolbar?.setNavigationOnClickListener { finish() }

        edtAdress.setOnTouchListener { v, event ->

            if (edtAdress.hasFocus()){

                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_SCROLL -> {
                        v.parent.requestDisallowInterceptTouchEvent(false)
                        return@setOnTouchListener true
                    }
                }

            }


            return@setOnTouchListener false
        }

    }

}
