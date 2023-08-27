package com.fmt.compose.eyepetizer.pages.daily.repository

import androidx.compose.runtime.MutableState
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fmt.compose.eyepetizer.ext.errorToast
import com.fmt.compose.eyepetizer.http.ApiService
import com.fmt.compose.eyepetizer.model.Item
import kotlinx.coroutines.delay

class DailyPagingSource(
    private val bannerList: MutableList<Item>,
    private var refreshing: MutableState<Boolean>,
) : PagingSource<String, Item>() {

    override fun getRefreshKey(state: PagingState<String, Item>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Item> {
        return try {
            val pageKey: String? = params.key
            var nextKey: String? = null
            val nextPageUrl: String?
            val daily = if (pageKey.isNullOrEmpty()) {
                refreshing.value = true
                ApiService.getDaily()
            } else {
                ApiService.getDaily(pageKey)
            }
            nextPageUrl = daily.nextPageUrl
            val itemList = daily.issueList[0].itemList
            itemList.removeAll {
                it.type == "banner2"
            }
            if (pageKey.isNullOrEmpty()) {
                bannerList.clear()
                bannerList.addAll(itemList)
            }
            if (!nextPageUrl.isNullOrEmpty()) {
                nextKey = nextPageUrl
            }
            refreshing.value = false
            LoadResult.Page(if (pageKey.isNullOrEmpty()) listOf(Item()) else itemList,
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