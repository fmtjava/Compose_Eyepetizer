@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package com.fmt.compose.eyepetizer.pages.daily

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.rememberAsyncImagePainter
import com.fmt.compose.eyepetizer.R
import com.fmt.compose.eyepetizer.ext.loadMoreView
import com.fmt.compose.eyepetizer.model.Item
import com.fmt.compose.eyepetizer.model.ItemData
import com.fmt.compose.eyepetizer.pages.daily.viewmodel.DailyViewModel
import com.fmt.compose.eyepetizer.pages.detail.VideoDetailActivity
import com.fmt.compose.eyepetizer.ui.theme.*
import com.fmt.compose.eyepetizer.util.DateUtils
import kotlinx.coroutines.launch
import java.util.*

const val TEXT_HEADER_TYPE = "textHeader"

@Composable
fun DailyPage(viewModel: DailyViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val lazyPagingItems = viewModel.pageFlow.collectAsLazyPagingItems()
    val pullRefreshState =
        rememberPullRefreshState(refreshing = viewModel.refreshing.value, onRefresh = {
            lazyPagingItems.refresh()
        })

    Column(Modifier.fillMaxSize()) {
        TitleBarWidget()
        Box(modifier = Modifier
            .pullRefresh(pullRefreshState)
            .zIndex(-1f)) {
            LazyColumn(Modifier.fillMaxSize()) {
                itemsIndexed(lazyPagingItems) { index, pagingItem ->
                    if (index == 0) {
                        SwiperWidget(viewModel.bannerList)
                    } else if (pagingItem?.type == TEXT_HEADER_TYPE) {
                        TitleItemWidget(pagingItem.data!!)
                    } else {
                        RankItemWidget(pagingItem?.data!!)
                    }
                }
                loadMoreView(lazyPagingItems)
            }

            PullRefreshIndicator(refreshing = viewModel.refreshing.value,
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        translationY = -160f
                    })
        }
    }
}

@Composable
internal fun TitleBarWidget() {
    TopAppBar(title = {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.daily_paper),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center)
        }
    }, actions = {
        IconButton(onClick = { }) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Black_87)
        }
    }, backgroundColor = Color.White)
}

@Composable
fun SwiperWidget(banners: List<Item>) {
    val virtualCount = Int.MAX_VALUE
    val actualCount = banners.size
    val initialIndex = virtualCount / 2

    val pagerState = rememberPagerState(initialPage = initialIndex)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(modifier = Modifier
        .padding(15.dp)
        .fillMaxWidth()
        .clip(RoundedCornerShape(4.dp))) {

        HorizontalPager(
            pageCount = virtualCount,
            state = pagerState,
        ) { index ->
            val actualIndex = (index - initialIndex).floorMod(actualCount)
            Image(painter = rememberAsyncImagePainter(model = banners[actualIndex].data!!.cover.feed),
                contentDescription = null,
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(7 / 3f)
                    .clickable {
                        VideoDetailActivity.start(context, banners[actualIndex].data!!)
                    },
                contentScale = ContentScale.Crop)

            DisposableEffect(Unit) {
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }, 3000, 3000)
                onDispose {
                    timer.cancel()
                }
            }
        }

        Row(Modifier
            .height(30.dp)
            .fillMaxWidth()
            .background(Black_12)
            .padding(start = 10.dp, end = 10.dp)
            .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            val actualIndex = (pagerState.currentPage - initialIndex).floorMod(actualCount)
            Text(text = banners[actualIndex].data!!.title, fontSize = 12.sp, color = Color.White)
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(banners.size) { iteration ->
                    val color = if (actualIndex == iteration) Color.White else White_12
                    Box(modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp))
                }
            }
        }
    }
}

@Composable
fun TitleItemWidget(itemData: ItemData) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 5.dp, bottom = 5.dp),
        contentAlignment = Alignment.Center) {
        Text(text = itemData.text ?: "",
            color = Black_87,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RankItemWidget(itemData: ItemData) {
    val context = LocalContext.current
    Column(modifier = Modifier
        .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 10.dp)
        .clickable {
            VideoDetailActivity.start(context, itemData)
        }) {
        Box {
            Image(painter = rememberAsyncImagePainter(model = itemData.cover.feed),
                contentDescription = null, modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(
                        RoundedCornerShape(4.dp)), contentScale = ContentScale.Crop)
            Box(modifier = Modifier
                .padding(start = 15.dp, top = 10.dp)
                .background(White_54, shape = CircleShape)
                .size(44.dp), contentAlignment = Alignment.Center) {
                Text(text = itemData.category, color = Color.White)
            }
            Box(modifier = Modifier
                .padding(end = 15.dp, bottom = 10.dp)
                .background(Black_54, shape = RoundedCornerShape(5.dp))
                .padding(5.dp)
                .align(Alignment.BottomEnd), contentAlignment = Alignment.Center) {
                Text(text = DateUtils.formatDateMsByMS((itemData.duration * 1000).toLong()),
                    color = Color.White,
                    fontWeight = FontWeight.Bold)
            }
        }
        Row(modifier = Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Image(painter = rememberAsyncImagePainter(model = itemData.author?.icon),
                contentDescription = null, modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape))
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)) {
                Text(text = itemData.title,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold)
                Text(text = itemData.author?.name ?: "",
                    fontSize = 12.sp,
                    color = UnselectedItemColor,
                    modifier = Modifier.padding(top = 2.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2)
            }
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Black_38)
            }
        }
        Divider(thickness = 0.5.dp, modifier = Modifier.padding(top = 5.dp))
    }
}

fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other = other) * other
}

