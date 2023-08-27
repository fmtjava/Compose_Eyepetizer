package com.fmt.compose.eyepetizer.pages.discover.viewmodel

import com.fmt.compose.eyepetizer.ext.AbsPagingViewModel
import com.fmt.compose.eyepetizer.http.ApiService
import com.fmt.compose.eyepetizer.model.BaseApiResult
import com.fmt.compose.eyepetizer.model.RecommendItem

const val HORIZONTAL_SCROLL_CARD = "horizontalScrollCard"

class RecommendViewModel : AbsPagingViewModel<RecommendItem>() {
    override suspend fun doLoadPage(pageKey: String?): BaseApiResult<RecommendItem> {
        return if (pageKey.isNullOrEmpty()) {
            val recommend = ApiService.getRecommendList()
            recommend.itemList.removeAll {
                it.type == HORIZONTAL_SCROLL_CARD
            }
            recommend
        } else {
            val recommend = ApiService.getRecommendList(pageKey)
            recommend.itemList.removeAll {
                it.type == HORIZONTAL_SCROLL_CARD
            }
            recommend
        }
    }

}