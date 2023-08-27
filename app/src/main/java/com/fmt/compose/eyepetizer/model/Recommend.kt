package com.fmt.compose.eyepetizer.model

class Recommend : BaseApiResult<RecommendItem>()

data class RecommendItem(val type: String, val data: RecommendItemData)

data class RecommendItemData(val content: Item)
