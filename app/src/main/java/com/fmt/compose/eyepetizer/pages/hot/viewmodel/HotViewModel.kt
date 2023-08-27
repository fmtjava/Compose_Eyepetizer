package com.fmt.compose.eyepetizer.pages.hot.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fmt.compose.eyepetizer.ext.errorToast
import com.fmt.compose.eyepetizer.http.ApiService
import com.fmt.compose.eyepetizer.model.Tab
import kotlinx.coroutines.launch

class HotViewModel : ViewModel() {

    var tabList = mutableStateListOf<Tab>()
    var selectedIndex = mutableStateOf(0)

    init {
        getTabList()
    }

    private fun getTabList() {
        viewModelScope.launch {
            runCatching {
                val tabs = ApiService.getHotTabList().tabInfo.tabList
                if (tabs.isNotEmpty()) {
                    tabList.clear()
                    tabList.addAll(tabs)
                }
            }.onFailure {
                errorToast(it.message ?: "")
            }
        }
    }
}