package com.fmt.compose.eyepetizer.ext

import android.widget.Toast
import com.fmt.compose.eyepetizer.mainApplication

fun errorToast(errorMsg: String) {
    Toast.makeText(mainApplication, errorMsg, Toast.LENGTH_LONG).show()
}