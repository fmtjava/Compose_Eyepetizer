@file:OptIn(ExperimentalMaterialApi::class)

package com.fmt.compose.eyepetizer.pages.discover

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.fmt.compose.eyepetizer.model.Category
import com.fmt.compose.eyepetizer.pages.discover.viewmodel.CategoryViewModel

@Composable
fun CategoryPage(viewModel: CategoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val pullRefreshState =
        rememberPullRefreshState(refreshing = viewModel.refreshing.value, onRefresh = {
            viewModel.getCategoryList()
        })
    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(pullRefreshState)) {
        LazyVerticalGrid(modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 5.dp)) {
            items(viewModel.categoryList) { item ->
                CategoryItemWidget(item)
            }
        }
        PullRefreshIndicator(refreshing = viewModel.refreshing.value,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun CategoryItemWidget(category: Category) {
    val context = LocalContext.current
    Box(Modifier
        .fillMaxWidth()
        .padding(horizontal = 5.dp, vertical = 5.dp)
        .clickable {
            CategoryDetailActivity.start(context, category.id, category.name, category.headerImage)
        },
        contentAlignment = Alignment.Center) {
        Image(painter = rememberAsyncImagePainter(model = category.bgPicture),
            contentDescription = null, modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(4.dp)), contentScale = ContentScale.Crop)
        Text(text = "#${category.name}",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center)
    }
}
