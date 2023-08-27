package com.fmt.compose.eyepetizer.pages.discover

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat

class CategoryDetailActivity : ComponentActivity() {

    val defaultBg =
        "http://ali-img.kaiyanapp.com/bcf6da5da1363e140ddbd536147f9931.jpeg?imageMogr2/quality/60/format/jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 沉浸式状态栏
        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val categoryId = intent.getIntExtra(KEY_CATEGORY_ID, 0)
        val categoryName = intent.getStringExtra(KEY_CATEGORY_NAME) ?: ""
        val headImage = intent.getStringExtra(KEY_HEAD_IMAGE) ?: ""
        setContent {
            CategoryDetailPage(categoryId, categoryName, headImage)
        }
    }

    companion object {
        private const val KEY_CATEGORY_ID = "categoryId"
        const val KEY_CATEGORY_NAME = "categoryName"
        const val KEY_HEAD_IMAGE = "headImage"
        fun start(context: Context, categoryId: Int, categoryName: String, headImage: String) {
            val intent = Intent(context, CategoryDetailActivity::class.java)
            intent.putExtra(KEY_CATEGORY_ID, categoryId)
            intent.putExtra(KEY_CATEGORY_NAME, categoryName)
            intent.putExtra(KEY_HEAD_IMAGE, headImage)
            context.startActivity(intent)
        }
    }
}