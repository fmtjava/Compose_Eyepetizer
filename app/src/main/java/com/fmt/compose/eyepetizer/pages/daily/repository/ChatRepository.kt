package com.fmt.compose.eyepetizer.pages.daily.repository

import com.fmt.compose.eyepetizer.model.BaiLianMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun streamChat(messages: List<BaiLianMessage>): Flow<String>
}