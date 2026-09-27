package com.fmt.compose.eyepetizer.model

import androidx.compose.runtime.Immutable
import java.util.UUID

/** 会话消息的发送方，用于决定消息气泡的布局和接口角色。 */
enum class ChatRole {
    USER,
    ASSISTANT
}

/**
 * UI 层使用的不可变聊天消息。
 *
 * [isStreaming] 仅表示助手消息仍可能收到增量内容；它不参与发送给服务端的历史上下文。
 */
@Immutable
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: ChatRole,
    val content: String,
    val isStreaming: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
