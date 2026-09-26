package com.fmt.compose.eyepetizer.model

import androidx.compose.runtime.Immutable
import java.util.UUID

enum class ChatRole {
    USER,
    ASSISTANT
}

@Immutable
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: ChatRole,
    val content: String,
    val isStreaming: Boolean = false // 用于判断是显示加载框等
)
