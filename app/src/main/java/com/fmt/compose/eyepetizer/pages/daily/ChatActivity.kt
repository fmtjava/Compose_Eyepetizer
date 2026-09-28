package com.fmt.compose.eyepetizer.pages.daily

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowInsetsControllerCompat

/**
    AI Chat

    输入问题
    ↓
    多轮上下文
    ↓
    百炼 SSE
    ↓
    流式展示
    ↓
    Markdown
    ↓
    Room 持久化
    ↓
    App 重启恢复
    ↓
    New Chat
    ↓
    conversationId 隔离上下文
    ↓
    时间分隔不同 Conversation
 */

/** 承载 [ChatPage] 的独立 Activity，并配置与浅色页面相匹配的状态栏样式。 */
class ChatActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
        setContent {
            ChatPage()
        }
    }

    companion object {
        /** Activity 的页面转场由清单中统一配置的主题提供。 */
        fun start(context: Context) {
            val intent = Intent(context, ChatActivity::class.java)
            context.startActivity(intent)
        }
    }
}
