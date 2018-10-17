package com.codefuelindia.dnote.Common

import com.codefuelindia.dnote.Model.InserDebitData
import com.codefuelindia.dnote.Model.ResCommon
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AddDebit {

      @POST("addDebit/")
      fun addDebitData(@Body insertDebit:InserDebitData):Call<ResCommon>

}