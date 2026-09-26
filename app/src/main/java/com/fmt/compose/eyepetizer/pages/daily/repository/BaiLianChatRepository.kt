package com.fmt.compose.eyepetizer.pages.daily.repository

import com.fmt.compose.eyepetizer.config.AppModule
import com.fmt.compose.eyepetizer.config.BaiLianConfig
import com.fmt.compose.eyepetizer.ext.fromJson
import com.fmt.compose.eyepetizer.ext.toJson
import com.fmt.compose.eyepetizer.model.BaiLianMessage
import com.fmt.compose.eyepetizer.model.ChatCompletionChunk
import com.fmt.compose.eyepetizer.model.ChatCompletionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
    HTTP Request
    ↓
    response.body.source()
    ↓
    逐行读取 SSE
    ↓
    data: {...}
    ↓
    ChatCompletionChunk
    ↓
    choices.firstOrNull()
    ↓
    delta.content
    ↓
    Flow<String>
 */
class BaiLianChatRepository() : ChatRepository {

    /**
     *  流失输出：https://docs.bailian.console.aliyun.com/zh/model-studio/stream
     */
    override fun streamChat(messages: List<BaiLianMessage>): Flow<String> = flow {
        val requestBody = ChatCompletionRequest(
            model = BaiLianConfig.MODEL,
            messages = messages,
            stream = true
        )

        val requestJson = toJson(requestBody)

        val request = Request.Builder()
            .url(BaiLianConfig.CHAT_URL)
            .header(
                "Authorization",
                "Bearer ${BaiLianConfig.APIKEY}"
            )
            .header(
                "Content-Type",
                "application/json"
            )
            .post(
                requestJson.toRequestBody(
                    "application/json; charset=utf-8".toMediaType()
                )
            )
            .build()

        AppModule.okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()

                throw IOException(
                    "Bailian request failed: " +
                            "${response.code} $errorBody"
                )
            }

            val source = response.body?.source()
            // 判断流是否读完（没有更多字节可读）
            source?.exhausted()?.let {
                while (!it) {
                    val line = source.readUtf8Line() ?: break
                    if (!line.startsWith("data:")) {
                        continue
                    }

                    val data = line
                        .removePrefix("data:")
                        .trim()

                    if (data.isEmpty()) {
                        continue
                    }

                    if (data == "[DONE]") {
                        break
                    }

                    val chunk =
                        runCatching { fromJson<ChatCompletionChunk>(data) }.getOrNull()
                            ?: continue

                    val content = chunk
                        .choices
                        .firstOrNull()
                        ?.delta
                        ?.content

                    if (!content.isNullOrEmpty()) {
                        emit(content)
                    }
                }
            }
        }
    }.flowOn(Dispatchers.IO)
}