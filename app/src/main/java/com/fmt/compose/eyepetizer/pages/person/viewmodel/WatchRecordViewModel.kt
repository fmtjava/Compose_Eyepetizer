package com.fmt.compose.eyepetizer.pages.person.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmt.compose.eyepetizer.db.CacheManager
import com.fmt.compose.eyepetizer.db.Video
import com.fmt.compose.eyepetizer.ext.fromJson
import com.fmt.compose.eyepetizer.model.ItemData
import kotlinx.coroutines.launch

class WatchRecordViewModel : ViewModel() {

    val videoList = mutableStateListOf<ItemData>()

    fun getVideoList() {
        viewModelScope.launch {
            val videos = CacheManager.get().videoDao.getVideoList()
            if (videos.isNotEmpty()) {
                videoList.clear()
                videoList.addAll(videos.map<Video, ItemData> { fromJson(it.content) }.toList())
            }
        }
    }

    fun deleteVideo(video: Video) {
        viewModelScope.launch {
            CacheManager.get().videoDao.delete(video)
            videoList.removeAll {
                it.id == video.videoId
            }
        }
    }

}