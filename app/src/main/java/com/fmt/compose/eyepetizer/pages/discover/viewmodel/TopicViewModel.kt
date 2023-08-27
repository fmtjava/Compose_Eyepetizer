package com.fmt.compose.eyepetizer.pages.discover.viewmodel

import com.fmt.compose.eyepetizer.ext.AbsPagingViewModel
import com.fmt.compose.eyepetizer.http.ApiService
import com.fmt.compose.eyepetizer.model.BaseApiResult
import com.fmt.compose.eyepetizer.model.Topic

class TopicViewModel : AbsPagingViewModel<Topic>() {

    override suspend fun doLoadPage(pageKey: String?): BaseApiResult<Topic> {
        return  if (pageKey.isNullOrEmpty()) {
            ApiService.getTopicList()
        } else {
            ApiService.getTopicList(pageKey)
        }
    }

}