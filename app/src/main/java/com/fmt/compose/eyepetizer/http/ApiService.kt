package com.fmt.compose.eyepetizer.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://baobab.kaiyanapp.com/api/"

private val retrofit by lazy {
    Retrofit.Builder()
        .baseUrl(
            BASE_URL
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

object ApiService : IApi by retrofit.create(
    IApi::
    class.java
)