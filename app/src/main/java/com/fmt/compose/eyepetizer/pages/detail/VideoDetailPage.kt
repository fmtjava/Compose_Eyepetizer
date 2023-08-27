@file:OptIn(ExperimentalMaterialApi::class)

package com.fmt.compose.eyepetizer.pages.detail

import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.jzvd.JzvdStd
import coil.compose.rememberAsyncImagePainter
import com.fmt.compose.eyepetizer.ext.toPx
import com.fmt.compose.eyepetizer.model.ItemData
import com.fmt.compose.eyepetizer.pages.detail.viewmodel.VideoDetailModel
import com.fmt.compose.eyepetizer.ui.theme.Black_54
import com.fmt.compose.eyepetizer.util.DateUtils
import com.fmt.compose.eyepetizer.util.ScreenUtils

const val VIDEO_SMALL_CARD_TYPE = "videoSmallCard"

@Composable
internal fun VideoDetailPage(itemData: ItemData?) {
    val context = LocalContext.current
    val viewModel: VideoDetailModel = viewModel()
    val pullRefreshState =
        rememberPullRefreshState(refreshing = viewModel.refreshing.value, onRefresh = {
            itemData?.run {
                viewModel.getRelateVideoList(itemData.id)
            }
        })
    itemData?.run {
        LaunchedEffect(key1 = Unit) {
            viewModel.getRelateVideoList(itemData.id)
            viewModel.saveVideo(itemData)
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        itemData?.run {
            Image(painter = rememberAsyncImagePainter(model = "${itemData.cover.blurred}/thumbnail/${ScreenUtils.instance.getScreenHeight()}x${ScreenUtils.instance.getScreenWidth()}"),
                contentDescription = null,
                Modifier.fillMaxSize(),
                contentScale = ContentScale.FillHeight)
        }
        itemData?.run {
            Column(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = {
                        val jzvdStd = JzvdStd(context)
                        jzvdStd.layoutParams =
                            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200.toPx())
                        jzvdStd
                    }
                ) {
                    it.setUp(itemData.playUrl, itemData.title)
                    it.startVideoAfterPreloading()
                }
                Box(modifier = Modifier
                    .pullRefresh(pullRefreshState)
                    .zIndex(-1f)) {
                    LazyColumn {
                        item {
                            VideoInfoItemWidget(itemData)
                        }
                        items(viewModel.itemList) {
                            if (it.type == VIDEO_SMALL_CARD_TYPE) {
                                VideoRelateItemWidget(it.data)
                            } else {
                                Text(
                                    text = it.data?.text ?: "",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                    PullRefreshIndicator(refreshing = viewModel.refreshing.value,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter))
                }
            }
        }
    }
}

@Composable
fun VideoInfoItemWidget(itemData: ItemData?) {
    itemData?.run {
        Column(Modifier.fillMaxWidth()) {
            Text(text = itemData.title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp))
            Text(text = "${itemData.category} / ${DateUtils.formatDateMsByYMDHM(itemData.author?.latestReleaseTime ?: 0)}",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp))
            Text(text = itemData.description,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 10.dp, top = 10.dp))
            Row(modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.White)
                Text(text = "${itemData.consumption.collectionCount}",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 3.dp))
                Icon(imageVector = Icons.Outlined.Share,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 30.dp)
                        .size(14.dp),
                    tint = Color.White)
                Text(text = "${itemData.consumption.shareCount}",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 3.dp))
                Icon(imageVector = Icons.Outlined.Comment,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 30.dp)
                        .size(14.dp),
                    tint = Color.White)
                Text(text = "${itemData.consumption.replyCount}",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 3.dp))
            }
            Divider(thickness = 0.5.dp,
                color = Color.White,
                modifier = Modifier.padding(top = 10.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {
                Image(painter = rememberAsyncImagePainter(model = itemData.author?.icon),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(40.dp)
                        .clip(CircleShape))
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp)) {
                    Text(text = itemData.author?.name ?: "",
                        color = Color.White,
                        fontSize = 15.sp)
                    Text(text = itemData.author?.description ?: "",
                        color = Color.White,
                        fontSize = 13.sp, modifier = Modifier.padding(top = 3.dp))
                }
                Text(text = stringResource(id = com.fmt.compose.eyepetizer.R.string.add_follow),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White)
                        .padding(5.dp))
            }
            Divider(thickness = 0.5.dp,
                color = Color.White)
        }
    }
}

@Composable
fun VideoRelateItemWidget(itemData: ItemData?) {
    val context = LocalContext.current
    itemData?.run {

        Row(modifier = Modifier
            .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 5.dp)
            .clickable {
                VideoDetailActivity.start(context, itemData)
            }) {
            Box {
                Image(painter = rememberAsyncImagePainter(model = itemData.cover.detail),
                    contentDescription = null, modifier = Modifier
                        .width(135.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(5.dp)), contentScale = ContentScale.Crop)
                Text(
                    text = DateUtils.formatDateMsByMS(itemData.duration.toLong() * 1000),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(end = 5.dp, bottom = 5.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Black_54)
                        .padding(3.dp)
                        .align(Alignment.BottomEnd)
                )
            }
            Column(modifier = Modifier
                .padding(10.dp)
                .weight(1f)) {
                Text(
                    text = itemData.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Text(
                    text = "#${itemData.category} / ${itemData.author?.name}",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 15.dp)
                )
            }
        }
    }
}
