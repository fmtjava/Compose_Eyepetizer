package com.fmt.compose.eyepetizer.pages.discover.viewmodel

import com.fmt.compose.eyepetizer.ext.AbsPagingViewModel
import com.fmt.compose.eyepetizer.http.ApiService
import com.fmt.compose.eyepetizer.model.BaseApiResult
import com.fmt.compose.eyepetizer.model.NewsItem

class NewsViewModel : AbsPagingViewModel<NewsItem>() {

    override suspend fun doLoadPage(pageKey: String?): BaseApiResult<NewsItem> {
        return if (pageKey.isNullOrEmpty()) {
            ApiService.getNewsList()
        } else {
            ApiService.getNewsList("${pageKey}&vc=6030000&deviceModel=Android")
        }
    }
}