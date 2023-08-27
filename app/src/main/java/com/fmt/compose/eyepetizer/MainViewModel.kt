package com.fmt.compose.eyepetizer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val tabs =
        mutableStateOf(listOf(
            TabItem(R.string.daily_paper, R.mipmap.home_ic_normal, R.mipmap.home_ic_selected),
            TabItem(R.string.discover,
                R.mipmap.home_ic_discovery_normal,
                R.mipmap.home_ic_discovery_selected),
            TabItem(R.string.hot, R.mipmap.home_ic_hot_normal, R.mipmap.home_ic_hot_selected),
            TabItem(R.string.mime, R.mipmap.home__ic_mine_normal, R.mipmap.home_ic_mine_selected),
        ))

    var selectTabIndex = mutableStateOf(0)

}