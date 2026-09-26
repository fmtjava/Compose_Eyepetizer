package com.fmt.compose.eyepetizer.pages.daily.state

import com.fmt.compose.eyepetizer.model.ChatMessage

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isGenerating: Boolean = false,
    val error: String? = null
)