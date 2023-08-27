package com.fmt.compose.eyepetizer.pages.discover.viewmodel

import com.fmt.compose.eyepetizer.ext.AbsPagingViewModel
import com.fmt.compose.eyepetizer.http.ApiService
import com.fmt.compose.eyepetizer.http.BASE_URL
import com.fmt.compose.eyepetizer.model.BaseApiResult
import com.fmt.compose.eyepetizer.model.Item

class CategoryDetailViewModel : AbsPagingViewModel<Item>() {

    var categoryId: Int = 0

    override suspend fun doLoadPage(pageKey: String?): BaseApiResult<Item> {
        return if (pageKey.isNullOrEmpty()) {
            val url =
                "${BASE_URL}v4/categories/videoList?id=${categoryId}&udid=d2807c895f0348a180148c9dfa6f2feeac0781b5&deviceModel=Android"
            ApiService.getCategoryVideoList(url)
        } else {
            ApiService.getFollowList("${pageKey}&udid=d2807c895f0348a180148c9dfa6f2feeac0781b5&deviceModel=Android")
        }
    }
}