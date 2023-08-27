@file:OptIn(ExperimentalMaterialApi::class)

package com.fmt.compose.eyepetizer.pages.discover

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.rememberAsyncImagePainter
import com.fmt.compose.eyepetizer.ext.loadMoreView
import com.fmt.compose.eyepetizer.model.NewsItem
import com.fmt.compose.eyepetizer.pages.detail.NewsDetailActivity
import com.fmt.compose.eyepetizer.pages.discover.viewmodel.NewsViewModel
import com.fmt.compose.eyepetizer.ui.theme.News_bg

const val TEXT_CARD = "textCard"

@Composable
fun NewsPage(viewModel: NewsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val lazyPagingItems = viewModel.pageFlow.collectAsLazyPagingItems()
    val pullRefreshState =
        rememberPullRefreshState(refreshing = viewModel.refreshing.value, onRefresh = {
            lazyPagingItems.refresh()
        })

    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 10.dp)) {
            itemsIndexed(lazyPagingItems) { _, pagingItem ->
                NewsItemWidget(pagingItem!!)
            }
            loadMoreView(pagingItems = lazyPagingItems)
        }

        PullRefreshIndicator(refreshing = viewModel.refreshing.value,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter))
    }
}

@Composable
fun NewsItemWidget(newsItem: NewsItem) {
    val context = LocalContext.current
    if (newsItem.type == TEXT_CARD) {
        Text(text = newsItem.data.text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black, modifier = Modifier.padding(start = 10.dp, top = 5.dp))
    } else {
        Card(backgroundColor = News_bg, shape = RoundedCornerShape(4.dp), modifier = Modifier
            .padding(10.dp)
            .clickable {
                val actionUrl = newsItem.data.actionUrl
                val title = newsItem.data.titleList?.get(0) ?: ""
                var webUrl = Uri.decode(actionUrl.substring(actionUrl.indexOf("url")))
                webUrl = webUrl.substring(4)
                NewsDetailActivity.start(context, webUrl, title)
            }) {
            Column {
                Image(painter = rememberAsyncImagePainter(model = newsItem.data.backgroundImage),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.FillWidth)
                newsItem.data.titleList?.run {
                    newsItem.data.titleList.forEach { title ->
                        Text(text = title,
                            fontSize = 12.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 10.dp,
                                top = 5.dp,
                                end = 10.dp,
                                bottom = 5.dp))
                    }
                }
            }
        }
    }
}

