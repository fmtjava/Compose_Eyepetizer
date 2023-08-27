@file:OptIn(ExperimentalFoundationApi::class)

package com.fmt.compose.eyepetizer.pages.person

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.CommentBank
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fmt.compose.eyepetizer.R
import com.fmt.compose.eyepetizer.pages.person.viewmodel.PersonViewModel
import com.fmt.compose.eyepetizer.ui.theme.Black_26
import com.fmt.compose.eyepetizer.ui.theme.UnselectedItemColor

@Composable
fun PersonPage(viewModel: PersonViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    LazyColumn(modifier = Modifier
        .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 30.dp)) {
        item {
            ProfileHeaderWidget()
        }
        stickyHeader {
            ProfileTabWidget()
        }
        items(viewModel.titles) { title ->
            SettingWidget(title)
        }
    }
}

@Composable
fun ProfileHeaderWidget() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {

        Image(painter = painterResource(id = R.mipmap.ic_head_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .height(200.dp)
                .alpha(0.75f)
                .blur(20.dp),
            contentScale = ContentScale.FillBounds)

        Column(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = R.mipmap.home_ic_img_avatar),
                contentDescription = null,
                modifier = Modifier
                    .size(88.dp)
                    .clip(
                        CircleShape),
                contentScale = ContentScale.Crop)
            Text(text = stringResource(id = R.string.view_personal_homepage),
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(top = 10.dp))
        }
    }
}

@Composable
fun ProfileTabWidget() {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(Color.White)) {
        Row(modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.Collections,
                    contentDescription = null,
                    tint = Black_26, modifier = Modifier.size(14.dp))
                Text(
                    text = stringResource(id = R.string.collect), fontSize = 15.sp,
                    color = Black_26,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
            Canvas(modifier = Modifier.size(0.5.dp, 40.dp)) {
                drawLine(color = UnselectedItemColor,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.CommentBank,
                    contentDescription = null,
                    tint = Black_26, modifier = Modifier.size(14.dp))
                Text(
                    text = stringResource(id = R.string.comment), fontSize = 15.sp,
                    color = Black_26,
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        }
        Canvas(modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .height(0.5.dp)
        ) {
            drawLine(color = UnselectedItemColor,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height))
        }
    }
}

@Composable
fun SettingWidget(text: String) {
    val context = LocalContext.current
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 30.dp)
        .background(Color.White),
        contentAlignment = Alignment.Center) {
        Text(text = text,
            fontSize = 16.sp,
            color = Black_26,
            modifier = Modifier
                .clickable {
                    when (text) {
                        context.getString(R.string.watch_record) -> {
                            WatchRecordActivity.start(context)
                        }
                    }
                })
    }
}