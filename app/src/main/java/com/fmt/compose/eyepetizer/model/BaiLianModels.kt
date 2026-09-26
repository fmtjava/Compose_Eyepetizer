package com.fmt.compose.eyepetizer.model

/** 百炼聊天接口中的单条历史消息。 */
data class BaiLianMessage(
    val role: String,
    val content: String
)

/** 百炼兼容模式 `/chat/completions` 接口的请求体。 */
data class ChatCompletionRequest(
    val model: String,
    val messages: List<BaiLianMessage>,
    val stream: Boolean = true,

    val streamOptions: StreamOptions = StreamOptions()
)

/** 流式响应的附加选项；开启后末尾数据帧会携带 token 用量。 */
data class StreamOptions(
    val include_usage: Boolean = true
)

/** 服务器通过 SSE 分段返回的聊天完成数据块。 */
data class ChatCompletionChunk(
    val id: String? = null,
    val choices: List<Choice> = emptyList(),
    val usage: Usage? = null
)

/** 单个候选回复的增量内容及其结束原因。 */
data class Choice(
    val index: Int = 0,
    val delta: Delta = Delta(),

    val finish_reason: String? = null
)

/** SSE 数据块中实际发生变化的字段；文本内容位于 [content]。 */
data class Delta(
    val role: String? = null,
    val content: String? = null
)

/** 本次完成请求的 token 统计，通常只会出现在流式响应的末尾。 */
data class Usage(
    val prompt_tokens: Int = 0,

    val completion_tokens: Int = 0,

    val total_tokens: Int = 0
)
