package com.fmt.compose.eyepetizer.ext

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson by lazy { Gson() }

fun toJson(obj: Any): String {
    return gson.toJson(obj)
}

inline fun <reified T> fromJson(json: String): T {
    val type = object : TypeToken<T>() {}.type
    return gson.fromJson(json, type)
}
