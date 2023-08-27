package com.fmt.compose.eyepetizer.ext

import com.fmt.compose.eyepetizer.mainApplication

fun Int.toPx(): Int = (this * mainApplication.resources.displayMetrics.density + 0.5f).toInt()