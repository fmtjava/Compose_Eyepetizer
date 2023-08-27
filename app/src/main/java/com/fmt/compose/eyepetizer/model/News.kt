package com.fmt.compose.eyepetizer.model

class News : BaseApiResult<NewsItem>()

data class NewsItem(val type: String, val data: NewsItemData)

data class NewsItemData(
    val text: String = "", val titleList: List<String>? = null,
    val backgroundImage: String = "", val actionUrl: String = "",
)