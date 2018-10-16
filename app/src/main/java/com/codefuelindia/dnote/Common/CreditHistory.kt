package com.codefuelindia.dnote.Common

import com.codefuelindia.dnote.Model.History
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CreditHistory {

    @POST("userCreditHistory/")
    @FormUrlEncoded
    fun getCreditHistory(@Field("id") id: String): Call<List<History>>


    @POST("userDebitHistory/")
    @FormUrlEncoded
    fun getDebittHistory(@Field("id") id: String): Call<List<History>>

}