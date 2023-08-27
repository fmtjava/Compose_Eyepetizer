@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)

package com.fmt.compose.eyepetizer.pages.hot

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fmt.compose.eyepetizer.R
import com.fmt.compose.eyepetizer.model.Tab
import com.fmt.compose.eyepetizer.pages.daily.RankItemWidget
import com.fmt.compose.eyepetizer.pages.hot.viewmodel.HotTabViewModel
import com.fmt.compose.eyepetizer.pages.hot.viewmodel.HotViewModel
import com.fmt.compose.eyepetizer.ui.theme.UnselectedItemColor
import kotlinx.coroutines.launch

@Composable
fun HotPage(viewModel: HotViewModel = viewModel()) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        viewModel.selectedIndex.value = pagerState.currentPage
    }

    Column(Modifier.fillMaxSize()) {
        TitleBarWidget(stringResource(id = R.string.hot))
        HotTabRowWidget(viewModel.tabList, viewModel.selectedIndex.value) { index ->
            coroutineScope.launch {
                viewModel.selectedIndex.value = index
                pagerState.animateScrollToPage(index)
            }
        }
        HotTabPageWidget(viewModel.tabList, pagerState, modifier = Modifier
            .weight(1f)
            .zIndex(-1f))
    }
}

@Composable
fun TitleBarWidget(title: String) {
    TopAppBar(title = {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            Text(text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        }
    }, backgroundColor = Color.White)
}

@Composable
fun HotTabRowWidget(
    tabs: List<Tab>,
    selectedIndex: Int,
    onTabClick: (Int) -> Unit,
) {
    if (tabs.isEmpty()) {
        return
    }

    TabRow(selectedTabIndex = selectedIndex,
        backgroundColor = Color.White,
        indicator = { tabPositions ->
            Box(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                contentAlignment = Alignment.Center) {
                Divider(modifier = Modifier.width(80.dp),
                    thickness = 3.dp,
                    color = LocalContentColor.current)
            }
        }) {
        tabs.forEachIndexed { index, tabInfo ->
            Tab(selected = selectedIndex == index, onClick = {
                onTabClick(index)
            }, text = {
                Text(text = tabInfo.name, fontSize = 14.sp)
            }, selectedContentColor = Color.Black, unselectedContentColor = UnselectedItemColor)
        }
    }
}

@Composable
fun HotTabPageWidget(
    tabs: List<Tab>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(pageCount = tabs.size, state = pagerState, modifier = modifier) { pageIndex ->
        TabHotWidget(tabs[pageIndex])
    }
}

@Composable
fun TabHotWidget(tab: Tab) {
    val viewModel: HotTabViewModel = viewModel(key = tab.name)
    LaunchedEffect(Unit) {
        viewModel.getTabData(tab.apiUrl)
    }

    val pullRefreshState =
        rememberPullRefreshState(refreshing = viewModel.refreshing.value, onRefresh = {
            viewModel.getTabData(tab.apiUrl, isRefresh = true)
        })
    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        LazyColumn(contentPadding = PaddingValues(top = 5.dp)) {
            items(viewModel.itemList) { item ->
                item.data?.let { RankItemWidget(itemData = item.data) }
            }
        }
        PullRefreshIndicator(refreshing = viewModel.refreshing.value,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter))
    }
}