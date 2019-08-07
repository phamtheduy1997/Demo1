package com.rikkei.pets.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response

interface DogApi{
    @GET("breeds/list/all")
    fun DogBreed(): Call<String>

}