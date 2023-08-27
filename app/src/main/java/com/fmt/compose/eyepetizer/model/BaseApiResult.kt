package com.fmt.compose.eyepetizer.model

open class BaseApiResult<T : Any>(
    val itemList: MutableList<T> = mutableListOf(),
    val nextPageUrl: String? = null,
)