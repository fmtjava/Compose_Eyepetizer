@file:OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)

package com.fmt.compose.eyepetizer.pages.discover

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.fmt.compose.eyepetizer.ext.items
import com.fmt.compose.eyepetizer.ext.loadMoreView
import com.fmt.compose.eyepetizer.model.RecommendItem
import com.fmt.compose.eyepetizer.pages.discover.viewmodel.RecommendViewModel
import com.fmt.compose.eyepetizer.ui.theme.Black_87
import com.fmt.compose.eyepetizer.util.ScreenUtils

const val VIDEO_TYPE = "video"

@Composable
fun RecommendPage(viewModel: RecommendViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val lazyPagingItems = viewModel.pageFlow.collectAsLazyPagingItems()
    val pullRefreshState =
        rememberPullRefreshState(refreshing = viewModel.refreshing.value, onRefresh = {
            lazyPagingItems.refresh()
        })
    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)
    ) {
        LazyVerticalStaggeredGrid(modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 5.dp,
                vertical = 5.dp),
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 10.dp) {
            items(lazyPagingItems) { pagingItem ->
                RecommendItemWidget(pagingItem)
            }
            loadMoreView(lazyPagingItems)
        }

        PullRefreshIndicator(refreshing = viewModel.refreshing.value,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter))
    }
}

@Composable
fun RecommendItemWidget(recommendItem: RecommendItem) {
    Card(shape = RoundedCornerShape(5.dp),
        backgroundColor = Color.White,
        modifier = Modifier
            .padding(horizontal = 5.dp)) {
        val maxWidth = ScreenUtils.instance.getScreenWidth()
        val width =
            if (recommendItem.data.content.data!!.width == 0) maxWidth else recommendItem.data.content.data.width
        val height =
            if (recommendItem.data.content.data.height == 0) maxWidth else recommendItem.data.content.data.height
        Column {
            Box {
                Image(painter = rememberAsyncImagePainter(model = recommendItem.data.content.data.cover.feed
                    ?: ""),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(width / height.toFloat()),
                    contentScale = ContentScale.FillWidth)
                recommendItem.data.content.data.urls?.run {
                    if (isNotEmpty()) {
                        Icon(imageVector = if (recommendItem.data.content.type == VIDEO_TYPE) Icons.Default.PlayCircle else Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(start = 5.dp, top = 5.dp))
                    }
                }
            }
            Text(text = recommendItem.data.content.data.description,
                fontSize = 14.sp,
                color = Black_87,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp))
            Row(Modifier
                .padding(horizontal = 6.dp, vertical = 10.dp)
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = rememberAsyncImagePainter(model = recommendItem.data.content.data.owner?.avatar
                        ?: ""), contentDescription = null, modifier = Modifier
                        .size(24.dp)
                        .clip(
                            CircleShape))
                    Text(text = recommendItem.data.content.data.owner?.nickname ?: "",
                        fontSize = 12.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(start = 3.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray)
                    Text(text = "${recommendItem.data.content.data.consumption.collectionCount}",
                        fontSize = 12.sp, modifier = Modifier.padding(start = 3.dp))
                }
            }
        }
    }
}

