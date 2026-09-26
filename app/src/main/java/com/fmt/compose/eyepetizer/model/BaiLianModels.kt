package com.fmt.compose.eyepetizer.model


data class BaiLianMessage(
    val role: String,
    val content: String
)

data class ChatCompletionRequest(
    val model: String,
    val messages: List<BaiLianMessage>,
    val stream: Boolean = true,

    val streamOptions: StreamOptions = StreamOptions()
)

data class StreamOptions(
    val include_usage: Boolean = true
)

data class ChatCompletionChunk(
    val id: String? = null,
    val choices: List<Choice> = emptyList(),
    val usage: Usage? = null
)

data class Choice(
    val index: Int = 0,
    val delta: Delta = Delta(),

    val finish_reason: String? = null
)

data class Delta(
    val role: String? = null,
    val content: String? = null
)

data class Usage(
    val prompt_tokens: Int = 0,

    val completion_tokens: Int = 0,

    val total_tokens: Int = 0
)
