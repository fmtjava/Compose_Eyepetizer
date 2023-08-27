package com.fmt.compose.eyepetizer.pages.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowInsetsControllerCompat
import cn.jzvd.Jzvd
import com.fmt.compose.eyepetizer.R
import com.fmt.compose.eyepetizer.model.ItemData

class VideoDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.BLACK
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }
        val itemData = intent.getParcelableExtra<ItemData>(KEY_ITEM_DATA)
        setContent {
            VideoDetailPage(itemData)
        }
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }

    companion object {
        const val KEY_ITEM_DATA = "key_item_data"
        fun start(context: Context, itemData: ItemData) {
            val intent = Intent(context, VideoDetailActivity::class.java)
            intent.putExtra(KEY_ITEM_DATA, itemData)
            context.startActivity(intent)
        }
    }
}