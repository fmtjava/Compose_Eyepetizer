package com.fmt.compose.eyepetizer.pages.daily.repository

import com.fmt.compose.eyepetizer.model.BaiLianMessage
import kotlinx.coroutines.flow.Flow

/**
 * 聊天服务的数据边界。
 *
 * 每个 [Flow] 元素都是助手回复的一个文本增量，调用方按顺序拼接即可展示流式内容。
 */
interface ChatRepository {

    fun streamChat(messages: List<BaiLianMessage>): Flow<String>
}
