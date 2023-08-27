package com.fmt.compose.eyepetizer.model

data class Daily(val issueList: MutableList<Issue>, val nextPageUrl: String? = null)