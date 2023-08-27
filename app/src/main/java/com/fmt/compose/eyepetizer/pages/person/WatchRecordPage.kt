@file:OptIn(ExperimentalFoundationApi::class)

package com.fmt.compose.eyepetizer.pages.person

import android.app.Activity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.fmt.compose.eyepetizer.R
import com.fmt.compose.eyepetizer.db.Video
import com.fmt.compose.eyepetizer.ext.toJson
import com.fmt.compose.eyepetizer.model.ItemData
import com.fmt.compose.eyepetizer.pages.detail.VideoDetailActivity
import com.fmt.compose.eyepetizer.pages.person.viewmodel.WatchRecordViewModel
import com.fmt.compose.eyepetizer.ui.theme.Black_26
import com.fmt.compose.eyepetizer.ui.theme.Black_54
import com.fmt.compose.eyepetizer.ui.theme.Black_87
import com.fmt.compose.eyepetizer.util.DateUtils
import com.fmt.compose.eyepetizer.view.Swipe
import com.fmt.compose.eyepetizer.view.TopTitleAppBar
import com.fmt.compose.eyepetizer.view.rememberSwipeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun WatchRecordPage(viewModel: WatchRecordViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.getVideoList()
    }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(Modifier.fillMaxSize()) {
        TopTitleAppBar(title = stringResource(id = R.string.watch_record)) {
            if (context is Activity) {
                context.finish()
            }
        }
        LazyColumn {
            items(viewModel.videoList) { videoItem ->
                VideoItemWidget(modifier = Modifier.animateItemPlacement(),
                    videoItem,
                    coroutineScope) {
                    viewModel.deleteVideo(Video(it.id, toJson(it)))
                }
            }
        }
    }
}

@Composable
fun VideoItemWidget(
    modifier: Modifier = Modifier,
    itemData: ItemData?,
    scope: CoroutineScope,
    onDelete: (itemData: ItemData) -> Unit,
) {
    val context = LocalContext.current
    val swipeState = rememberSwipeState()
    itemData?.run {
        Swipe(modifier = modifier, state = swipeState, background = {
            Box(modifier = Modifier
                .width(66.dp)
                .fillMaxHeight()
                .background(Color.Red)
                .clickable {
                    scope.launch {
                        swipeState.close()
                    }
                    onDelete(itemData)
                }, contentAlignment = Alignment.Center) {
                Text(text = "删除", color = Color.White)
            }
        }) {
            Row(modifier = Modifier
                .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 5.dp)
                .clickable {
                    VideoDetailActivity.start(context, itemData)
                }) {
                Box {
                    Image(painter = rememberAsyncImagePainter(model = itemData.cover.detail),
                        contentDescription = null,
                        modifier = Modifier
                            .width(135.dp)
                            .height(80.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        contentScale = ContentScale.Crop)
                    Text(text = DateUtils.formatDateMsByMS(itemData.duration.toLong() * 1000),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .padding(end = 5.dp, bottom = 5.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Black_54)
                            .padding(3.dp)
                            .align(Alignment.BottomEnd))
                }
                Column(modifier = Modifier
                    .padding(10.dp)
                    .weight(1f)) {
                    Text(
                        text = itemData.title,
                        color = Black_87,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                    Text(text = "#${itemData.category} / ${itemData.author?.name}",
                        color = Black_26,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 15.dp))
                }
            }
        }
    }
}