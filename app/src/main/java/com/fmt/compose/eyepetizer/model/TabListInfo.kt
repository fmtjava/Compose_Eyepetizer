package com.fmt.compose.eyepetizer.model

data class TabListInfo(val tabInfo: TabInfo)

data class TabInfo(val tabList: List<Tab>)

data class Tab(val name: String, val apiUrl: String)