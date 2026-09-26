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
 * 百炼兼容模式聊天接口的实现。
 *
 * 它在 IO 调度器中同步读取 SSE 响应，过滤非 `data:` 行和协议结束标记，解析每个数据块的
 * `delta.content` 后作为 [Flow] 文本增量发出。
 */
class BaiLianChatRepository() : ChatRepository {

    /**
     * 发起启用流式输出的请求，并将 SSE 数据帧转换为内容增量。
     *
     * HTTP 非成功状态会以 [IOException] 失败，使 ViewModel 能结束占位消息并显示错误。
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
            // SSE 帧可能包含角色、用量等非文本字段；只有有效的内容增量才向上游发出。
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
