package com.fmt.compose.eyepetizer.pages.detail.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmt.compose.eyepetizer.db.CacheManager
import com.fmt.compose.eyepetizer.db.Video
import com.fmt.compose.eyepetizer.ext.errorToast
import com.fmt.compose.eyepetizer.ext.toJson
import com.fmt.compose.eyepetizer.http.ApiService
import com.fmt.compose.eyepetizer.model.Item
import com.fmt.compose.eyepetizer.model.ItemData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VideoDetailModel : ViewModel() {

    val itemList = mutableStateListOf<Item>()
    var refreshing = mutableStateOf(false)

    fun getRelateVideoList(videoId: Int) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                val items = ApiService.getRelateVideoList(videoId).itemList
                if (items.isNotEmpty()) {
                    itemList.clear()
                    itemList.addAll(items)
                }
                refreshing.value = false
            }.onFailure {
                errorToast(it.message ?: "")
                delay(200)
                refreshing.value = false
            }
        }
    }

    fun saveVideo(itemData: ItemData) {
        viewModelScope.launch {
            runCatching {
                val video = CacheManager.get().videoDao.getVideo(itemData.id)
                if (video == null) {
                    CacheManager.get().videoDao.save(Video(itemData.id, toJson(itemData)))
                }
            }
        }
    }
}