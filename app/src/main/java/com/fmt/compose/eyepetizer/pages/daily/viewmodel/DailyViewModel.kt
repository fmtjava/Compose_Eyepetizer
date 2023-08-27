package com.fmt.compose.eyepetizer.pages.daily.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.fmt.compose.eyepetizer.model.Item
import com.fmt.compose.eyepetizer.pages.daily.repository.DailyPagingSource

class DailyViewModel : ViewModel() {

    var bannerList = mutableStateListOf<Item>()
    var refreshing = mutableStateOf(false)

    val pageFlow = Pager(config = PagingConfig(pageSize = 10,
        initialLoadSize = 10,
        prefetchDistance = 1), pagingSourceFactory = {
        DailyPagingSource(bannerList, refreshing)
    }).flow.cachedIn(viewModelScope)

}