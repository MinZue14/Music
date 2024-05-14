package com.example.music.API

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiInterface {

    @Headers("X-RapidAPI-Key: 3cc3f8f8e9msh6aea4eea6419b2dp155d04jsn36af6698f4f2",
            "X-RapidAPI-Host: deezerdevs-deezer.p.rapidapi.com")

    @GET("search")
    fun getData(@Query("q") query: String): Call<MyData>
}