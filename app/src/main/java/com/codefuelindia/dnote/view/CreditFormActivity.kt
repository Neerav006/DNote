package com.codefuelindia.dnote.view

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import com.codefuelindia.dnote.R

import kotlinx.android.synthetic.main.activity_credit_form.*

class CreditFormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_form)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar?.title = "Credit form"
        toolbar?.setNavigationOnClickListener { finish() }

    }

}
