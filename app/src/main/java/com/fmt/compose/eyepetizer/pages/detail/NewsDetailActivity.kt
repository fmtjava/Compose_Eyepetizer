package com.fmt.compose.eyepetizer.pages.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsControllerCompat
import com.fmt.compose.eyepetizer.view.TopTitleAppBar

class NewsDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
        val webUrl = intent.getStringExtra(KEY_URL) ?: ""
        val title = intent.getStringExtra(KEY_TITLE) ?: ""
        setContent {
            Column(Modifier.fillMaxSize()) {
                TopTitleAppBar(title = title) {
                    finish()
                }
                AndroidView(factory = {
                    WebView(this@NewsDetailActivity).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT)
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        loadUrl(webUrl)
                    }
                })
            }
        }
    }

    companion object {
        const val KEY_URL = "url"
        const val KEY_TITLE = "title"
        fun start(context: Context, url: String, title: String) {
            val intent = Intent(context, NewsDetailActivity::class.java)
            intent.putExtra(KEY_URL, url)
            intent.putExtra(KEY_TITLE, title)
            context.startActivity(intent)
        }
    }
}