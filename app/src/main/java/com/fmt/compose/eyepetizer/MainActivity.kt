@file:OptIn(ExperimentalFoundationApi::class)

package com.fmt.compose.eyepetizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fmt.compose.eyepetizer.pages.daily.DailyPage
import com.fmt.compose.eyepetizer.pages.discover.DiscoverPage
import com.fmt.compose.eyepetizer.pages.hot.HotPage
import com.fmt.compose.eyepetizer.pages.person.PersonPage
import com.fmt.compose.eyepetizer.ui.theme.SelectedItemColor
import com.fmt.compose.eyepetizer.ui.theme.UnselectedItemColor
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
        }
        setContent {
            val pagerState = rememberPagerState()
            Scaffold(backgroundColor = Color.White, bottomBar = {
                BottomNavigationBar(pagerState)
            }) { padding ->
                ContentScreen(padding, pagerState)
            }
        }
    }
}

@Composable
fun ContentScreen(padding: PaddingValues, pagerState: PagerState) {
    HorizontalPager(pageCount = 4,
        userScrollEnabled = false,
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(padding)) { pageIndex ->
        when (pageIndex) {
            0 -> DailyPage()
            1 -> DiscoverPage()
            2 -> HotPage()
            3 -> PersonPage()
        }
    }
}

@Composable
fun BottomNavigationBar(pagerState: PagerState) {
    val viewModel: MainViewModel = viewModel()
    val scope = rememberCoroutineScope()

    BottomNavigation(backgroundColor = Color.White) {
        viewModel.tabs.value.forEachIndexed { index, tabItem ->
            BottomNavigationItem(selected = viewModel.selectTabIndex.value == index, onClick = {
                viewModel.selectTabIndex.value = index
                scope.launch {
                    pagerState.scrollToPage(index)
                }
            }, label = {
                Text(text = stringResource(id = tabItem.title),
                    color = if (viewModel.selectTabIndex.value == index) SelectedItemColor else UnselectedItemColor)
            }, icon = {
                Icon(painter = painterResource(id = if (viewModel.selectTabIndex.value == index) tabItem.selectIcon else tabItem.normalIcon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp))
            })
        }
    }
}

data class TabItem(
    @StringRes val title: Int,
    @DrawableRes val normalIcon: Int,
    @DrawableRes val selectIcon: Int,
)
