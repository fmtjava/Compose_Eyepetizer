package com.fmt.compose.eyepetizer.pages.daily.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmt.compose.eyepetizer.db.CacheManager
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

    /** 限制流式回复触发 Markdown 重解析和页面重组的频率，同时保留打字反馈。 */
    private companion object {
        const val STREAM_UI_UPDATE_INTERVAL_MS = 100L
    }

    /** 延迟创建仓储，避免 ViewModel 初始化时立即构造网络相关对象。 */
    private val repository: BaiLianChatRepository by lazy { BaiLianChatRepository(CacheManager.get().chatMessageDao) }

    /** 可变状态仅在 ViewModel 内部持有，页面只能订阅只读状态流。 */
    private val _uiState = MutableStateFlow(ChatUiState())

    val uiState = _uiState.asStateFlow()

    /** 在请求前裁剪历史消息，控制单次请求的上下文大小。 */
    private val contextBuilder = ContextBuilder(maxChars = 30_000)

    /** 当前流式请求的协程，用于支持用户主动停止生成。 */
    private var generateJob: Job? = null

    /** 当前会话 ID。*/
    private var currentConversationId: String? = null

    init {
        loadHistory()
    }

    /**
     *  加载历史数据
     */
    private fun loadHistory() {
        viewModelScope.launch {
            runCatching {
                val messages = repository.loadMessages()
                // conversationId 仅在客户端给消息分组；百炼兼容接口不接收该字段。
                // 发送时会以它筛选同一会话的消息，并将筛选结果放进 request.messages。
                val latestConversationId = repository.getLatestConversationId()
                currentConversationId = latestConversationId ?: UUID.randomUUID().toString()
                _uiState.update {
                    it.copy(
                        messages = messages,
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                // 即使历史加载失败，
                // 也允许用户开始一个新会话
                currentConversationId = UUID.randomUUID().toString()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            }
        }
    }

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
        val conversationId =
            currentConversationId
                ?: UUID.randomUUID()
                    .toString()
                    .also {
                        currentConversationId = it
                    }
        // 固定本次发送所属会话，用户消息、助手占位消息和请求上下文都使用同一个 ID。
        // 用户消息会立即显示；助手占位消息随后接收每个 SSE 文本增量。
        val userMessage = ChatMessage(
            role = ChatRole.USER,
            content = content,
            conversationId = conversationId
        )

        val assistantMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
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
         * 非常重要：构造百炼上下文时只取当前 Conversation
         *
         * assistantMessage 此时还是空字符串，
         * 不能把它发送给百炼。
         *
         * conversationId 不会作为接口字段发送；它只用于从本地消息中选出当前会话的
         * 历史记录，ContextBuilder 再将这些记录转换为百炼所需的 messages 数组。
         */
        val requestMessages =
            contextBuilder.build(
                _uiState.value.messages
                    .filter { message ->
                        // String 用 == 比较内容。历史消息与当前会话 ID 来自不同的
                        // Room 查询，即使值相同，也不能依赖 === 的对象引用相同。
                        message.conversationId == conversationId
                    }
                    .filterNot {
                        it.id == assistantMessage.id
                    }
            )

        generateJob = viewModelScope.launch {
            val pendingDeltas = StringBuilder()
            var lastUiUpdateAt = 0L

            // 多个 SSE 小片段合并为一次 UI 更新，避免长 Markdown 在每个 delta 到达时
            // 都重新解析与测量。正常完成或请求失败前都会把剩余内容刷新出来。
            fun flushPendingDeltas() {
                if (pendingDeltas.isEmpty()) {
                    return
                }
                appendAssistantContent(
                    id = assistantMessage.id,
                    delta = pendingDeltas.toString(),
                )
                pendingDeltas.setLength(0)
                lastUiUpdateAt = SystemClock.uptimeMillis()
            }

            try {
                // 保存用户消息
                repository.saveMessage(message = userMessage)
                // AI 流式请求
                repository
                    .streamChat(requestMessages)
                    .collect { delta ->
                        pendingDeltas.append(delta)
                        if (
                            SystemClock.uptimeMillis() - lastUiUpdateAt >=
                            STREAM_UI_UPDATE_INTERVAL_MS
                        ) {
                            flushPendingDeltas()
                        }
                    }
                flushPendingDeltas()
                finishAssistantMessage(
                    assistantMessage.id
                )
                // AI 完成后再保存
                saveAssistantMessage(
                    assistantMessage.id
                )
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    throw e
                }
                flushPendingDeltas()
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

    /**
     *  保存大模型返回的消息
     */
    private suspend fun saveAssistantMessage(
        id: String
    ) {
        val message =
            _uiState.value.messages
                .firstOrNull {
                    it.id == id
                }
                ?: return

        if (message.content.isBlank()) {
            return
        }

        repository.saveMessage(
            message =
                message.copy(
                    isStreaming = false
                )
        )
    }

    /** 取消进行中的请求并将页面状态恢复为初始会话。 */
    fun clearConversation() {
        generateJob?.cancel()
        _uiState.value = ChatUiState()
    }

    // =========================
    // 新建对话
    // =========================
    fun newChat() {
        // 取消旧对话正在进行的请求
        generateJob?.cancel()
        generateJob = null

        currentConversationId = UUID.randomUUID().toString()

        // 清空当前会话状态
        _uiState.update {
            it.copy(isLoading = false, isGenerating = false, error = null)
        }
    }
}
