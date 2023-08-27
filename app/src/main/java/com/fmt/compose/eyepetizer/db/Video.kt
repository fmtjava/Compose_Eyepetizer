package com.fmt.compose.eyepetizer.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video")
data class Video(@PrimaryKey val videoId: Int, val content: String)