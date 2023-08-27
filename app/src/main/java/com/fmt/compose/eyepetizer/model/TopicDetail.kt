package com.fmt.compose.eyepetizer.model

data class TopicDetail(
    val headerImage: String = "",
    val brief: String = "",
    val text: String = "",
    val itemList: List<TopicItem> = listOf(),
)

data class TopicItem(val data: TopicItemData)

data class TopicItemData(val content: Item)