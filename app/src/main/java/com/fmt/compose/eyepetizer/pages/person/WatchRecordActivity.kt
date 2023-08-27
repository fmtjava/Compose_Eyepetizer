package com.fmt.compose.eyepetizer.pages.person

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowInsetsControllerCompat

class WatchRecordActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
        setContent {
            WatchRecordPage()
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, WatchRecordActivity::class.java)
            context.startActivity(intent)
        }
    }

}