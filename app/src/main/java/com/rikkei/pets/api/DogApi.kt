package com.t3h.demo1.api

import retrofit2.Call
import retrofit2.http.GET

interface DogApi{
    @GET("breeds/list/all")
    fun DogBreed(): Call<String>

}