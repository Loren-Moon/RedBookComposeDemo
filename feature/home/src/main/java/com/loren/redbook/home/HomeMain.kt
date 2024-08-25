@file:OptIn(ExperimentalFoundationApi::class)

package com.loren.redbook.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.loren.redbook.base.ContentItem
import com.loren.redbook.data.bean.ContentBean
import com.loren.redbook.data.ext.cancelRipperClick
import com.loren.redbook.theme.RedBookTheme
import com.loren.redbook.theme.view.AnimateHeaderLayout
import com.loren.redbook.theme.view.AnimateHeaderState
import com.loren.redbook.theme.view.CommonRefresh
import com.loren.redbook.theme.view.LazyVerticalStaggeredGridWithPaging
import com.loren.redbook.theme.view.RedBookTabRow
import com.loren.redbook.theme.view.rememberAnimateHeaderState
import com.loren.redbook.theme.view.rememberCommonRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeMain(
    snackBarHostState: SnackbarHostState,
    onSettingClick: () -> Unit,
    navigateToDetail: (ContentBean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val pageCount = stringArrayResource(id = R.array.home_tab).size
        val pagerState = rememberPagerState(initialPage = 1) {
            pageCount
        }
        HomeTabLayout(
            homeSelectedIndex = pagerState.settledPage,
            onTabClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(it)
                }
            },
            onSettingClick = onSettingClick
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(RedBookTheme.colors.window),
        ) {
            when (it) {
                1 -> {
                    FindLayout(coroutineScope, navigateToDetail)
                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "HomePage:$it",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .wrapContentSize(),
                            style = RedBookTheme.textStyle.titleLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FindLayout(
    coroutineScope: CoroutineScope,
    navigateToDetail: (ContentBean) -> Unit
) {
    val animateHeaderState = rememberAnimateHeaderState()
    val pageCount = stringArrayResource(id = R.array.home_find_tab).size
    val pagerState = rememberPagerState(initialPage = 0) {
        pageCount
    }

    AnimateHeaderLayout(
        modifier = Modifier.fillMaxSize(),
        state = animateHeaderState,
        topBar = {
            HomeFindTabLayout(
                homeFindSelectedIndex = pagerState.settledPage,
                onTabClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                },
            )
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = animateHeaderState.flag,
        ) { index ->
            when (index) {
                0 -> {
                    PageRecommendItem(animateHeaderState, navigateToDetail)
                }

                1 -> {
                    PageVideoItem(navigateToDetail)
                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "FindPage:$index",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .wrapContentSize(),
                            style = RedBookTheme.textStyle.titleLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PageVideoItem(
    navigateToDetail: (ContentBean) -> Unit
) {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val videoPagingItems = homeViewModel.videoPagingFlow.collectAsLazyPagingItems()
    val refreshState = rememberCommonRefreshState()
    val staggeredGridState = rememberLazyStaggeredGridState()

    LaunchedEffect(videoPagingItems.loadState.refresh) {
        when {
            videoPagingItems.loadState.refresh is LoadState.NotLoading -> {
                refreshState.isRefreshing = false
            }

            videoPagingItems.loadState.refresh is LoadState.Error -> {
                refreshState.isRefreshing = false
            }
        }
    }

    CommonRefresh(
        modifier = Modifier.fillMaxSize(),
        state = refreshState,
        onRefresh = {
            videoPagingItems.refresh()
        }
    ) {
        LazyVerticalStaggeredGridWithPaging(
            lazyPagingItems = videoPagingItems,
            state = staggeredGridState,
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalItemSpacing = 4.dp,
            modifier = Modifier.fillMaxSize()
        ) { bean, index ->
            ContentItem(bean, navigateToDetail)
        }
    }
}

@Composable
private fun PageRecommendItem(
    animateHeaderState: AnimateHeaderState,
    navigateToDetail: (ContentBean) -> Unit
) {
    val homeViewModel = hiltViewModel<HomeViewModel>()
    val homePagingItems = homeViewModel.commonPagingFlow.collectAsLazyPagingItems()

    val refreshState = rememberCommonRefreshState()
    val staggeredGridState = rememberLazyStaggeredGridState()

    LaunchedEffect(
        staggeredGridState.firstVisibleItemIndex,
        staggeredGridState.firstVisibleItemScrollOffset
    ) {
        if (!staggeredGridState.canScrollBackward) {
            animateHeaderState.flag = true
        }
        if (animateHeaderState.flag && staggeredGridState.firstVisibleItemIndex == 0 && staggeredGridState.firstVisibleItemScrollOffset >= animateHeaderState.topBarHeight) {
            animateHeaderState.flag = false
        }
    }

    LaunchedEffect(homePagingItems.loadState.refresh) {
        when {
            homePagingItems.loadState.refresh is LoadState.NotLoading -> {
                refreshState.isRefreshing = false
            }

            homePagingItems.loadState.refresh is LoadState.Error -> {
                refreshState.isRefreshing = false
            }
        }
    }

    CommonRefresh(
        modifier = Modifier.fillMaxSize(),
        state = refreshState,
        onRefresh = {
            homePagingItems.refresh()
        }
    ) {
        LazyVerticalStaggeredGridWithPaging(
            lazyPagingItems = homePagingItems,
            state = staggeredGridState,
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalItemSpacing = 4.dp,
            modifier = Modifier.fillMaxSize()
        ) { bean, index ->
            ContentItem(bean, navigateToDetail)
        }
    }
}

@Composable
private fun HomeFindTabLayout(
    homeFindSelectedIndex: Int,
    onTabClick: (index: Int) -> Unit
) {
    RedBookTabRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(RedBookTheme.colors.background)
            .height(40.dp),
        selectedIndex = homeFindSelectedIndex
    ) {
        stringArrayResource(id = R.array.home_find_tab).forEachIndexed { index, s ->
            val selected = homeFindSelectedIndex == index
            Box(contentAlignment = Alignment.Center) {
                // 占位
                Text(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .padding(horizontal = 8.dp),
                    text = s,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Transparent
                )
                Text(
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentWidth()
                        .padding(horizontal = 8.dp)
                        .cancelRipperClick {
                            onTabClick(index)
                        },
                    text = s,
                    lineHeight = 40.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = if (selected) 14.sp else 12.sp,
                    color = if (selected) RedBookTheme.colors.title else RedBookTheme.colors.body
                )
            }
        }
    }
}

@Composable
private fun HomeTabLayout(
    homeSelectedIndex: Int,
    onTabClick: (index: Int) -> Unit,
    onSettingClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        RedBookTabRow(
            modifier = Modifier.align(Alignment.Center),
            selectedIndex = homeSelectedIndex,
            indicator = {
                val strokeColor = RedBookTheme.colors.theme
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(20.dp, 2.dp)
                            .background(strokeColor, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        ) {
            stringArrayResource(id = R.array.home_tab).forEachIndexed { index, s ->
                val selected = homeSelectedIndex == index
                Box(
                    modifier = Modifier
                        .height(48.dp)
                        .wrapContentWidth()
                        .padding(horizontal = 16.dp)
                        .cancelRipperClick {
                            onTabClick(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = s,
                        style = RedBookTheme.textStyle.titleSmall,
                        color = if (selected) RedBookTheme.colors.title else RedBookTheme.colors.body
                    )
                }

            }
        }

        IconButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = {
                onSettingClick()
            }
        ) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(id = com.loren.redbook.data.R.drawable.icon_setting),
                contentDescription = "setting",
                tint = RedBookTheme.colors.icon
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            thickness = 0.1.dp,
            color = RedBookTheme.colors.divider
        )
    }
}

