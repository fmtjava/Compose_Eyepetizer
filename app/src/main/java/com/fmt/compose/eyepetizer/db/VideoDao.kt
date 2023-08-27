package com.fmt.compose.eyepetizer.db

import androidx.room.*

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(video: Video): Long

    @Query("select * from video where videoId=:videoId")
    suspend fun getVideo(videoId: Int): Video?

    @Query("select * from video")
    suspend fun getVideoList(): List<Video>

    @Delete
    suspend fun delete(video: Video): Int

}