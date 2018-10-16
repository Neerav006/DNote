package com.codefuelindia.dnote.Common

import com.codefuelindia.dnote.Model.Product
import retrofit2.Call
import retrofit2.http.POST

interface ProductListFetch {
    @POST("getProduct/")
    fun getProductList():Call<List<Product>>
}