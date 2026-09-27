package com.fmt.compose.eyepetizer.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,

    val conversationId: String,

    val role: String,

    val content: String,

    val createdAt: Long
)