package com.fmt.compose.eyepetizer.util

import com.fmt.compose.eyepetizer.mainApplication

class ScreenUtils private constructor() {

    private var screenWidth = 0
    private var screenHeight = 0

    init {
        screenWidth = mainApplication.resources.displayMetrics.widthPixels
        screenHeight = mainApplication.resources.displayMetrics.heightPixels
    }

    companion object {
        val instance by lazy { ScreenUtils() }
    }

    fun getScreenWidth(): Int {
        return screenWidth
    }

    fun getScreenHeight(): Int {
        return screenHeight
    }


}