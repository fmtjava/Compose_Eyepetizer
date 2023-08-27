@file:OptIn(ExperimentalMaterialApi::class)

package com.fmt.compose.eyepetizer.pages.discover

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.rememberAsyncImagePainter
import com.fmt.compose.eyepetizer.R
import com.fmt.compose.eyepetizer.ext.loadMoreView
import com.fmt.compose.eyepetizer.model.Item
import com.fmt.compose.eyepetizer.pages.detail.VideoDetailActivity
import com.fmt.compose.eyepetizer.pages.detail.VideoDetailPage
import com.fmt.compose.eyepetizer.pages.discover.viewmodel.FollowViewModel
import com.fmt.compose.eyepetizer.ui.theme.Black_26
import com.fmt.compose.eyepetizer.ui.theme.Follow_bg
import com.fmt.compose.eyepetizer.ui.theme.White_54
import com.fmt.compose.eyepetizer.util.DateUtils

@Composable
fun FollowPage(viewModel: FollowViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
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
                FollowItemWidget(pagingItem!!)
            }
            loadMoreView(lazyPagingItems)
        }
        PullRefreshIndicator(refreshing = viewModel.refreshing.value,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun FollowItemWidget(item: Item) {
    Column(modifier = Modifier
        .padding(start = 15.dp, bottom = 10.dp)
        .fillMaxWidth()) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Image(painter = rememberAsyncImagePainter(model = item.data?.header?.icon),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape))

            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)) {
                Text(text = item.data?.header?.title ?: "",
                    style = TextStyle(color = Color.Black, fontSize = 14.sp))
                Text(text = item.data?.header?.description ?: "",
                    style = TextStyle(color = Black_26, fontSize = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 3.dp))
            }
            Text(text = stringResource(id = R.string.add_follow),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Follow_bg)
                    .padding(5.dp))
        }
        item.data?.itemList?.run {
            LazyRow {
                itemsIndexed(item.data.itemList) { _, itemData ->
                    FollowHorizontalItemWidget(itemData)
                }
            }
        }
        Divider(thickness = 0.5.dp, modifier = Modifier.padding(top = 10.dp))
    }
}

@Composable
fun FollowHorizontalItemWidget(item: Item) {
    val current = LocalContext.current
    Column(modifier = Modifier
        .padding(end = 15.dp)
        .clickable {
            item.data?.run {
                VideoDetailActivity.start(current, item.data)
            }
        }) {
        Box {
            Image(painter = rememberAsyncImagePainter(model = item.data?.cover?.feed),
                contentDescription = null,
                modifier = Modifier
                    .width(300.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop)

            Text(text = item.data?.category ?: "",
                fontSize = 13.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 8.dp, end = 8.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(White_54)
                    .padding(6.dp)
                    .align(Alignment.TopEnd))
        }
        Text(text = item.data?.title ?: "",
            fontSize = 14.sp,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 3.dp))
        Text(text = DateUtils.formatDateMsByYMDHM(item.data!!.author?.latestReleaseTime ?: 0),
            fontSize = 12.sp,
            color = Black_26,
            modifier = Modifier.padding(top = 3.dp))
    }
}