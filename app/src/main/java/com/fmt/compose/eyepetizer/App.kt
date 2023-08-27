package com.fmt.compose.eyepetizer

import android.app.Application

lateinit var mainApplication: Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        mainApplication = this
    }
}