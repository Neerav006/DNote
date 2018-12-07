package com.codefuelindia.dnote.Common

import com.codefuelindia.dnote.Model.ResCommon
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AddCredit {

    @POST("addCredit/")
    @FormUrlEncoded
    fun addCreditData(@Field("u_id") u_id:String, @Field("id") id:String, @Field("amount") amount:String):Call<ResCommon>
}