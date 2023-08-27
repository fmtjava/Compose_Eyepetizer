package com.fmt.compose.eyepetizer.pages.discover

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberAsyncImagePainter
import com.fmt.compose.eyepetizer.pages.daily.RankItemWidget
import com.fmt.compose.eyepetizer.pages.discover.viewmodel.CategoryDetailViewModel
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

@Composable
internal fun CategoryDetailPage(categoryId: Int, categoryName: String, headImage: String) {
    val viewModel: CategoryDetailViewModel = viewModel()
    viewModel.categoryId = categoryId
    val lazyPagingItems = viewModel.pageFlow.collectAsLazyPagingItems()
    val context = LocalContext.current
    val state = rememberCollapsingToolbarScaffoldState()
    CollapsingToolbarScaffold(
        modifier = Modifier
            .fillMaxSize(),
        state = state,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbarModifier = Modifier.background(MaterialTheme.colors.primary),
        toolbar = {
            Image(
                painter =
                rememberAsyncImagePainter(model = headImage),
                modifier = Modifier
                    .parallax(0.5f)
                    .fillMaxWidth()
                    .height(200.dp)
                    .graphicsLayer {
                        alpha = state.toolbarState.progress
                    },
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .height(50.dp)
                    .pin(),
            ) {
                IconButton(modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = {
                        if (context is Activity) {
                            context.finish()
                        }
                    }) {
                    Icon(imageVector = Icons.Outlined.ArrowBack,
                        tint = Color.White,
                        contentDescription = null)
                }
            }
            Text(
                text = categoryName,
                modifier = Modifier
                    .road(Alignment.CenterStart, Alignment.BottomStart)
                    .padding(40.dp, 16.dp, 16.dp, 16.dp)
                    .statusBarsPadding(),
                color = Color.White,
                fontSize = (18 + (30 - 18) * state.toolbarState.progress).sp
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(lazyPagingItems) {
                it?.data?.run {
                    RankItemWidget(itemData = it.data)
                }
            }
        }
    }
}