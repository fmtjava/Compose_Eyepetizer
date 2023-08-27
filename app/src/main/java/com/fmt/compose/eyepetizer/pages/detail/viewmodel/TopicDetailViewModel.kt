package com.fmt.compose.eyepetizer.pages.detail.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmt.compose.eyepetizer.ext.errorToast
import com.fmt.compose.eyepetizer.http.ApiService
import com.fmt.compose.eyepetizer.model.TopicDetail
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TopicDetailViewModel : ViewModel() {

    var topicDetail = mutableStateOf(TopicDetail())
    var refreshing = mutableStateOf(false)

    fun getTopicDetail(topicId: Int) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                topicDetail.value = ApiService.getTopicDetail(topicId)
                refreshing.value = false
            }.onFailure {
                errorToast(it.message ?: "")
                delay(200)
                refreshing.value = false
            }
        }
    }

}