@file:OptIn(ExperimentalMaterialApi::class)

package com.fmt.compose.eyepetizer.pages.discover

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.rememberAsyncImagePainter
import com.fmt.compose.eyepetizer.ext.loadMoreView
import com.fmt.compose.eyepetizer.model.Topic
import com.fmt.compose.eyepetizer.pages.detail.TopicDetailActivity
import com.fmt.compose.eyepetizer.pages.discover.viewmodel.TopicViewModel

@Composable
fun TopicPage(viewModel: TopicViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val lazyPagingItems = viewModel.pageFlow.collectAsLazyPagingItems()
    val pullRefreshState =
        rememberPullRefreshState(refreshing = viewModel.refreshing.value, onRefresh = {
            lazyPagingItems.refresh()
        })

    Box(modifier = Modifier
        .pullRefresh(pullRefreshState)) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(lazyPagingItems) { _, pagingItem ->
                TopicItemWidget(pagingItem!!)
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
fun TopicItemWidget(topic: Topic) {
    val context = LocalContext.current
    Column(Modifier
        .fillMaxWidth()
        .clickable {
            TopicDetailActivity.start(context, topic.data.id)
        }) {
        Image(painter = rememberAsyncImagePainter(model = topic.data.image),
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(4.dp)), contentScale = ContentScale.Crop)

        Divider(thickness = 0.5.dp,
            modifier = Modifier.padding(start = 10.dp, end = 10.dp))
    }
}

