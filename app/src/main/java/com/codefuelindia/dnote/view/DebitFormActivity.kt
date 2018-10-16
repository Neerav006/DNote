package com.codefuelindia.dnote.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent
import com.codefuelindia.dnote.R

import kotlinx.android.synthetic.main.activity_debit_form.*
import kotlinx.android.synthetic.main.content_debit_form.*

class DebitFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debit_form)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.title = "Debit form"
        toolbar?.setNavigationOnClickListener { finish() }

        edtAdress.setOnTouchListener { v, event ->

            if (edtAdress.hasFocus()) {

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
