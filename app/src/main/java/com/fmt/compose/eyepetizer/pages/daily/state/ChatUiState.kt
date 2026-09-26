package com.fmt.compose.eyepetizer.pages.daily.state

import com.fmt.compose.eyepetizer.model.ChatMessage

/**
 * 聊天页的完整渲染状态。
 *
 * [isGenerating] 控制输入栏行为；[error] 仅记录最近一次流式请求失败的信息。
 */
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isGenerating: Boolean = false,
    val error: String? = null
)
