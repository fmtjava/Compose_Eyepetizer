package com.fmt.compose.eyepetizer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val type: String = "",
    val data: ItemData? = null,
) : Parcelable

@Parcelize
data class ItemData(
    val id: Int,
    val dataType: String,
    val text: String? = "",
    val description: String = "",
    val title: String,
    val category: String,
    val author: Author?,
    val cover: Cover,
    val duration: Int,
    val header: Header?,
    val itemList: List<Item>?,
    val width: Int,
    val height: Int,
    val owner: Owner?,
    val consumption: Consumption,
    val urls: List<String>?,
    val playUrl: String,
) : Parcelable

@Parcelize
data class Header(val id: Int, val icon: String, val title: String, val description: String) :
    Parcelable

@Parcelize
data class Author(
    val icon: String,
    val name: String,
    val description: String,
    val latestReleaseTime: Long,
) : Parcelable

@Parcelize
data class Cover(
    val feed: String,
    val blurred: String,
    val detail: String,
) : Parcelable

@Parcelize
data class Owner(
    val avatar: String,
    val nickname: String,
) : Parcelable

@Parcelize
data class Consumption(
    val collectionCount: Int,
    val shareCount: Int,
    val replyCount: Int,
) : Parcelable