@file:OptIn(ExperimentalFoundationApi::class)

package com.fmt.compose.eyepetizer.pages.discover

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fmt.compose.eyepetizer.R
import com.fmt.compose.eyepetizer.pages.discover.viewmodel.DiscoverViewModel
import com.fmt.compose.eyepetizer.pages.hot.TitleBarWidget
import com.fmt.compose.eyepetizer.ui.theme.UnselectedItemColor
import kotlinx.coroutines.launch

val tabs = listOf("关注", "分类", "专题", "资讯", "推荐")

@Composable
fun DiscoverPage(
    viewModel: DiscoverViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        viewModel.selectedIndex.value = pagerState.currentPage
    }

    Column(Modifier.fillMaxSize()) {
        TitleBarWidget(stringResource(id = R.string.discover))
        DiscoverTabRowWidget(viewModel.selectedIndex.value) { index ->
            coroutineScope.launch {
                viewModel.selectedIndex.value = index
                pagerState.animateScrollToPage(index)
            }
        }
        DiscoverTabPageWidget(pagerState, modifier = Modifier
            .weight(1f)
            .zIndex(-1f))
    }
}

@Composable
fun DiscoverTabRowWidget(
    selectedIndex: Int,
    onTabClick: (Int) -> Unit,
) {
    TabRow(selectedTabIndex = selectedIndex,
        backgroundColor = Color.White,
        indicator = { tabPositions ->
            Box(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                contentAlignment = Alignment.Center) {
                Divider(modifier = Modifier.width(60.dp),
                    thickness = 3.dp,
                    color = LocalContentColor.current)
            }
        }) {
        tabs.forEachIndexed { index, title ->
            Tab(selected = selectedIndex == index, onClick = {
                onTabClick(index)
            }, text = {
                Text(text = title, fontSize = 14.sp)
            }, selectedContentColor = Color.Black, unselectedContentColor = UnselectedItemColor)
        }
    }
}

@Composable
fun DiscoverTabPageWidget(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(pageCount = tabs.size, state = pagerState, modifier = modifier) { pageIndex ->
        when (pageIndex) {
            0 -> FollowPage()
            1 -> CategoryPage()
            2 -> TopicPage()
            3 -> NewsPage()
            4 -> RecommendPage()
        }
    }
}
