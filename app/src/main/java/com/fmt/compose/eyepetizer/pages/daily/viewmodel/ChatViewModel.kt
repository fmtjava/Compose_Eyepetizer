package com.fmt.compose.eyepetizer.pages.daily.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmt.compose.eyepetizer.model.ChatMessage
import com.fmt.compose.eyepetizer.model.ChatRole
import com.fmt.compose.eyepetizer.pages.daily.builder.ContextBuilder
import com.fmt.compose.eyepetizer.pages.daily.repository.BaiLianChatRepository
import com.fmt.compose.eyepetizer.pages.daily.state.ChatUiState
import java.util.UUID
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 协调聊天页面状态与百炼流式接口。
 *
 * 每次发送会先插入用户消息和空的助手占位消息，再把服务端返回的增量文本追加到该占位
 * 消息，因此 UI 不需要维护独立的“正在输入”文本。
 */
class ChatViewModel : ViewModel() {

    /** 延迟创建仓储，避免 ViewModel 初始化时立即构造网络相关对象。 */
    private val repository: BaiLianChatRepository by lazy { BaiLianChatRepository() }

    /** 可变状态仅在 ViewModel 内部持有，页面只能订阅只读状态流。 */
    private val _uiState = MutableStateFlow(ChatUiState())

    val uiState = _uiState.asStateFlow()

    /** 在请求前裁剪历史消息，控制单次请求的上下文大小。 */
    private val contextBuilder = ContextBuilder(maxChars = 30_000)

    /** 当前流式请求的协程，用于支持用户主动停止生成。 */
    private var generateJob: Job? = null

    /**
     * 发送一条用户消息并开始接收助手的流式回复。
     *
     * 空消息与已有生成任务会被忽略，以防止无效请求和并发回复交叉写入同一会话。
     */
    fun sendMessage(text: String) {
        val content = text.trim()

        if (content.isEmpty()) {
            return
        }

        if (_uiState.value.isGenerating) {
            return
        }
        // 用户消息会立即显示；助手占位消息随后接收每个 SSE 文本增量。
        val userMessage = ChatMessage(role = ChatRole.USER, content = content)

        val assistantMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = ChatRole.ASSISTANT,
            content = "",
            isStreaming = true
        )
        // 一次状态更新同时插入两条消息，页面能立即展示提问和“思考中”状态。
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
            contextBuilder.build(_uiState.value.messages.filterNot {
                it.id == assistantMessage.id
            })

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

    /** 将本次 SSE 返回的文本块追加到指定助手消息，形成逐字显示效果。 */
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

    /** 正常结束时关闭指定消息的流式标记，并恢复输入栏的发送状态。 */
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

    /** 异常结束时保留已经收到的内容，并将错误信息暴露给页面展示。 */
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

    /** 取消当前请求并把所有未完成的助手消息固定为已结束状态。 */
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

    /** 取消进行中的请求并将页面状态恢复为初始会话。 */
    fun clearConversation() {
        generateJob?.cancel()
        _uiState.value = ChatUiState()
    }
}
