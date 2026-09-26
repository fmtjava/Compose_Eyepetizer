package com.fmt.compose.eyepetizer.pages.daily.builder

import com.fmt.compose.eyepetizer.model.BaiLianMessage
import com.fmt.compose.eyepetizer.model.ChatMessage
import com.fmt.compose.eyepetizer.model.ChatRole

/**
 * 将 UI 消息转换为接口上下文，并以最近消息优先的策略限制总字符数。
 *
 * 从最新消息倒序收集可以优先保留当前问题附近的上下文，最后再恢复时间顺序以满足聊天
 * 接口的消息顺序要求。
 */
class ContextBuilder(private val maxChars: Int = 30_000) {

    /**
     * 忽略空消息；当下一条完整历史消息会超出 [maxChars] 时停止裁剪。
     *
     * 不截断单条消息，避免向模型传入残缺的对话内容。
     */
    fun build(messages: List<ChatMessage>): List<BaiLianMessage> {
        if (messages.isEmpty()) {
            return emptyList()
        }

        val result = mutableListOf<BaiLianMessage>()

        var currentChars = 0

        // 从最新消息向前寻找
        for (message in messages.asReversed()) {

            if (message.content.isBlank()) {
                continue
            }

            val contentLength = message.content.length

            /*
             * 当前消息加入后超过限制，
             * 就停止继续向前找历史消息。
             */
            if (currentChars + contentLength > maxChars) {
                break
            }

            result += BaiLianMessage(
                role = when (message.role) {

                    ChatRole.USER ->
                        "user"

                    ChatRole.ASSISTANT ->
                        "assistant"
                },
                content = message.content
            )

            currentChars += contentLength
        }

        /*
         * 上面是从后向前添加的，
         * 最终需要恢复正常时间顺序。
         */
        return result.reversed()
    }
}
