@file:OptIn(ExperimentalFoundationApi::class)

package com.fmt.compose.eyepetizer.ext

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridItemScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.fmt.compose.eyepetizer.R

@OptIn(ExperimentalFoundationApi::class)
fun <T : Any> LazyStaggeredGridScope.items(
    items: LazyPagingItems<T>,
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable LazyStaggeredGridItemScope.(item: T) -> Unit,
) {
    items(
        count = items.itemCount,
        key = if (key == null) null else { index ->
            val item = items.peek(index)
            if (item == null) {
                PagingPlaceholderKey(index)
            } else {
                key(item)
            }
        }
    ) { index ->
        items[index]?.let {
            itemContent(it)
        }
    }
}

@SuppressLint("BanParcelableUsage")
private data class PagingPlaceholderKey(private val index: Int) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<PagingPlaceholderKey> =
            object : Parcelable.Creator<PagingPlaceholderKey> {
                override fun createFromParcel(parcel: Parcel) =
                    PagingPlaceholderKey(parcel.readInt())

                override fun newArray(size: Int) = arrayOfNulls<PagingPlaceholderKey?>(size)
            }
    }
}

fun <T : Any> LazyListScope.loadMoreView(pagingItems: LazyPagingItems<T>) {
    when (pagingItems.loadState.append) {
        is LoadState.Loading -> item {
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator()
            }
        }
        is LoadState.Error -> item {
            Row(horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable {
                        pagingItems.retry()
                    }) {
                Text(stringResource(id = R.string.loading_error))
                Icon(imageVector = Icons.Outlined.Sync,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 5.dp))
            }
        }
        else -> {

        }
    }
}

fun <T : Any> LazyStaggeredGridScope.loadMoreView(pagingItems: LazyPagingItems<T>) {
    when (pagingItems.loadState.append) {
        is LoadState.Loading -> item {
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator()
            }
        }
        is LoadState.Error -> item {
            Row(horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable {
                        pagingItems.retry()
                    }) {
                Text(stringResource(id = R.string.loading_error))
                Icon(imageVector = Icons.Outlined.Sync,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 5.dp))
            }
        }
        else -> {

        }
    }
}