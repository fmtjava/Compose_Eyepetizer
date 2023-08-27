@file:OptIn(ExperimentalMaterialApi::class)

package com.fmt.compose.eyepetizer.pages.detail

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.fmt.compose.eyepetizer.model.TopicDetail
import com.fmt.compose.eyepetizer.pages.daily.RankItemWidget
import com.fmt.compose.eyepetizer.pages.detail.viewmodel.TopicDetailViewModel
import com.fmt.compose.eyepetizer.ui.theme.Black_12
import com.fmt.compose.eyepetizer.ui.theme.Border_bg
import com.fmt.compose.eyepetizer.ui.theme.UnselectedItemColor
import com.fmt.compose.eyepetizer.view.TopTitleAppBar

@Composable
fun TopicDetailPage(topicId: Int) {
    val context = LocalContext.current
    val viewModel: TopicDetailViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.getTopicDetail(topicId)
    }
    val pullRefreshState =
        rememberPullRefreshState(refreshing = viewModel.refreshing.value, onRefresh = {
            viewModel.getTopicDetail(topicId)
        })
    val topicDetail = viewModel.topicDetail.value
    Column(modifier = Modifier
        .background(Color.White)
        .fillMaxSize()) {
        TopTitleAppBar(topicDetail.brief) {
            if (context is Activity) {
                context.finish()
            }
        }
        Box(Modifier
            .fillMaxWidth()
            .weight(1f)
            .zIndex(-1f)) {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)) {
                item {
                    TopicDetailHeadWidget(topicDetail)
                }
                items(topicDetail.itemList) {
                    it.data.content.data?.run {
                        RankItemWidget(itemData = it.data.content.data)
                    }
                }
            }
            PullRefreshIndicator(refreshing = viewModel.refreshing.value,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}

@Composable
fun TopicDetailHeadWidget(topicDetail: TopicDetail) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
                painter = rememberAsyncImagePainter(model = topicDetail.headerImage),
                contentDescription = null,
                contentScale = ContentScale.FillWidth)
            Text(modifier = Modifier
                .padding(start = 20.dp,
                    top = 40.dp,
                    end = 20.dp,
                    bottom = 10.dp),
                text = topicDetail.text,
                fontSize = 12.sp,
                color = UnselectedItemColor)
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(Black_12))
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 230.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
            .border(1.dp, Border_bg),
            contentAlignment = Alignment.Center) {
            Text(text = topicDetail.brief,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black)
        }
    }
}
