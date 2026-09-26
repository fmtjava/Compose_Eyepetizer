package com.fmt.compose.eyepetizer.config

import okhttp3.OkHttpClient

object AppModule {

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build()
    }
}