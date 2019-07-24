package com.rikkei.pets.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ImageDogApi {
    @GET("breed/{name}/image/random")
    fun ImageDog(@Path("name") name: String ): Call<ResponseBody>


}