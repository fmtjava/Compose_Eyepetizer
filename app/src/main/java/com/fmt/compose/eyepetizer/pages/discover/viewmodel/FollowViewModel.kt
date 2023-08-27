package com.fmt.compose.eyepetizer.pages.discover.viewmodel

import com.fmt.compose.eyepetizer.ext.AbsPagingViewModel
import com.fmt.compose.eyepetizer.http.ApiService
import com.fmt.compose.eyepetizer.model.BaseApiResult
import com.fmt.compose.eyepetizer.model.Item

class FollowViewModel : AbsPagingViewModel<Item>() {
    override suspend fun doLoadPage(pageKey: String?): BaseApiResult<Item> {
        return if (pageKey.isNullOrEmpty()) {
            ApiService.getFollowList()
        } else {
            ApiService.getFollowList(pageKey)
        }
    }
}