package com.fmt.compose.eyepetizer.pages.daily.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmt.compose.eyepetizer.model.BaiLianMessage
import com.fmt.compose.eyepetizer.model.ChatMessage
import com.fmt.compose.eyepetizer.model.ChatRole
import com.fmt.compose.eyepetizer.pages.daily.repository.BaiLianChatRepository
import com.fmt.compose.eyepetizer.pages.daily.state.ChatUiState
import java.util.UUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val repository: BaiLianChatRepository by lazy { BaiLianChatRepository() }

    private val _uiState = MutableStateFlow(ChatUiState())

    val uiState = _uiState.asStateFlow()

    private var generateJob: Job? = null

    fun sendMessage(text: String) {
        val content = text.trim()

        if (content.isEmpty()) {
            return
        }

        if (_uiState.value.isGenerating) {
            return
        }
        // 构建请求的 UserMessage
        val userMessage = ChatMessage(role = ChatRole.USER, content = content)

        // 构建占位的 AssistantMessage
        val assistantMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = ChatRole.ASSISTANT,
            content = "",
            isStreaming = true
        )
        // 更新
        _uiState.update {
            it.copy(
                messages =
                    it.messages +
                            userMessage +
                            assistantMessage,
                isGenerating = true,
                error = null
            )
        }

        /*
         * 非常重要：
         *
         * assistantMessage 此时还是空字符串，
         * 不能把它发送给百炼。
         */
        val requestMessages =
            _uiState.value.messages
                .filterNot {
                    it.id == assistantMessage.id
                }
                .map {
                    BaiLianMessage(
                        role = when (it.role) {
                            ChatRole.USER ->
                                "user"

                            ChatRole.ASSISTANT ->
                                "assistant"
                        },
                        content = it.content
                    )
                }

        generateJob = viewModelScope.launch {
            try {
                repository
                    .streamChat(requestMessages)
                    .collect { delta ->
                        appendAssistantContent(
                            id = assistantMessage.id,
                            delta = delta
                        )
                    }
                finishAssistantMessage(
                    assistantMessage.id
                )
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    throw e
                }
                finishWithError(
                    assistantMessage.id,
                    e
                )
            }
        }
    }

    /**
     *  拼接流式返回的数据块
     */
    private fun appendAssistantContent(id: String, delta: String) {
        _uiState.update { state ->
            state.copy(
                messages = state.messages.map { message ->
                    if (message.id == id) {
                        message.copy(
                            content =
                                message.content + delta // 打字效果的关键点
                        )
                    } else {
                        message
                    }
                }
            )
        }
    }

    /**
     *  流式返回结束
     */
    private fun finishAssistantMessage(id: String) {
        _uiState.update { state ->
            state.copy(
                messages = state.messages.map {
                    if (it.id == id) {
                        it.copy(isStreaming = false)
                    } else {
                        it
                    }
                },
                isGenerating = false
            )
        }
    }

    /**
     *  流式返回异常场景
     */
    private fun finishWithError(id: String, throwable: Throwable) {
        _uiState.update { state ->
            state.copy(
                messages = state.messages.map {
                    if (it.id == id) {
                        it.copy(isStreaming = false)
                    } else {
                        it
                    }
                },
                isGenerating = false,
                error = throwable.message ?: "请求失败"
            )
        }
    }

    /**
     *  停止流失返回
     */
    fun stopGenerating() {
        generateJob?.cancel()
        _uiState.update { state ->
            state.copy(
                messages = state.messages.map {
                    if (it.role == ChatRole.ASSISTANT && it.isStreaming) {
                        it.copy(isStreaming = false)
                    } else {
                        it
                    }
                },
                isGenerating = false
            )
        }
    }

    /**
     *  清除对话
     */
    fun clearConversation() {
        generateJob?.cancel()
        _uiState.value = ChatUiState()
    }
}