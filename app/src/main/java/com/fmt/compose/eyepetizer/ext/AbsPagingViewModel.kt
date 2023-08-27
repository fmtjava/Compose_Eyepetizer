package com.fmt.compose.eyepetizer.ext

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.fmt.compose.eyepetizer.model.BaseApiResult
import kotlinx.coroutines.delay

abstract class AbsPagingViewModel<T : Any> : ViewModel() {

    var refreshing = mutableStateOf(false)

    val pageFlow = Pager(config = PagingConfig(pageSize = 10,
        initialLoadSize = 10,
        prefetchDistance = 1), pagingSourceFactory = {
        AbsPagingSource()
    }).flow.cachedIn(viewModelScope)

    inner class AbsPagingSource() : PagingSource<String, T>() {
        override fun getRefreshKey(state: PagingState<String, T>): String? = null

        override suspend fun load(params: LoadParams<String>): LoadResult<String, T> {
            return try {
                val pageKey: String? = params.key
                var nextKey: String? = null
                val nextPageUrl: String?
                val apiResult = if (pageKey.isNullOrEmpty()) {
                    refreshing.value = true
                    doLoadPage()
                } else {
                    doLoadPage(pageKey)
                }
                nextPageUrl = apiResult.nextPageUrl
                if (!nextPageUrl.isNullOrEmpty()) {
                    nextKey = nextPageUrl
                }
                refreshing.value = false
                LoadResult.Page(apiResult.itemList,
                    null,
                    nextKey)
            } catch (e: Exception) {
                delay(200)
                refreshing.value = false
                errorToast(e.message ?: "")
                LoadResult.Error(e)
            }
        }
    }

    abstract suspend fun doLoadPage(pageKey: String? = null): BaseApiResult<T>
}