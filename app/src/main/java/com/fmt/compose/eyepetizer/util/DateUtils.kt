package com.fmt.compose.eyepetizer.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val msDateFormat = SimpleDateFormat("mm:ss", Locale.CHINA)
    private val ymsDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
    private val ymdHsDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CHINA)

    fun formatDateMsByMS(milliseconds: Long): String {
        return msDateFormat.format(Date(milliseconds))
    }

    fun formatDateMsByYMD(milliseconds: Long): String {
        return ymsDateFormat.format(Date(milliseconds))
    }

    fun formatDateMsByYMDHM(milliseconds: Long): String {
        return ymdHsDateFormat.format(Date(milliseconds))
    }
}