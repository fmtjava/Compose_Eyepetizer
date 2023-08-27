package com.fmt.compose.eyepetizer.pages.hot.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmt.compose.eyepetizer.ext.errorToast
import com.fmt.compose.eyepetizer.http.ApiService
import com.fmt.compose.eyepetizer.model.Item
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HotTabViewModel : ViewModel() {

    var itemList = mutableStateListOf<Item>()
    var refreshing = mutableStateOf(false)

    fun getTabData(url: String, isRefresh: Boolean = false) {
        if (itemList.isEmpty() || isRefresh) {
            viewModelScope.launch {
                runCatching {
                    if (isRefresh) {
                        refreshing.value = true
                    }
                    itemList.clear()
                    itemList.addAll(ApiService.getHotTabData(url).itemList)
                    if (isRefresh) {
                        refreshing.value = false
                    }
                }.onFailure {
                    errorToast(it.message ?: "")
                    delay(200)
                    refreshing.value = false
                }
            }
        }
    }
}