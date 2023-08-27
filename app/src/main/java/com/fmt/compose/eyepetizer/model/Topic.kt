package com.fmt.compose.eyepetizer.model

class SpecialTopics : BaseApiResult<Topic>()

data class Topic(val data: TopicData)

data class TopicData(val id: Int, val image: String, val actionUrl: String)