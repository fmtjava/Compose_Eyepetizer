package com.fmt.compose.eyepetizer.pages.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowInsetsControllerCompat

class TopicDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
        val topicId = intent.getIntExtra(KEY_TOPIC_ID, 0)
        setContent {
            TopicDetailPage(topicId)
        }
    }

    companion object {
        private const val KEY_TOPIC_ID = "topicId"
        fun start(context: Context, topicId: Int) {
            val intent = Intent(context, TopicDetailActivity::class.java)
            intent.putExtra(KEY_TOPIC_ID, topicId)
            context.startActivity(intent)
        }
    }
}