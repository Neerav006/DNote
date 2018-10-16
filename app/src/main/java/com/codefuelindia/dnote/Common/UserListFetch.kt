package com.codefuelindia.dnote.Common

import com.codefuelindia.dnote.Model.User
import retrofit2.Call
import retrofit2.http.POST

interface UserListFetch {

    @POST("getUsers/")
    fun getUserList():Call<List<User>>

}