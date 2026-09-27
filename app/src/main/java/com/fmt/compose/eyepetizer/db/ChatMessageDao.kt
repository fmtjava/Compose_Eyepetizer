package com.fmt.compose.eyepetizer.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messageEntity: ChatMessageEntity)

    @Query(
        """
            select * from chat_messages order by createdAt asc
        """
    )
    suspend fun getAll(): List<ChatMessageEntity>

}