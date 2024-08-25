package com.loren.redbook.me

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.loren.redbook.base.ContentItem
import com.loren.redbook.data.R
import com.loren.redbook.data.bean.ContentBean
import com.loren.redbook.data.di.MockDataModule
import com.loren.redbook.data.ext.cancelRipperClick
import com.loren.redbook.theme.RedBookTheme
import com.loren.redbook.theme.view.CommonRefresh
import com.loren.redbook.theme.view.CommonRefreshHeader
import com.loren.redbook.theme.view.CommonRefreshState
import com.loren.redbook.theme.view.LazyVerticalStaggeredGridWithPaging
import com.loren.redbook.theme.view.RedBookTabRow
import com.loren.redbook.theme.view.rememberCommonRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeRoute(
    snackBarHostState: SnackbarHostState,
    navigateToContentDetail: (ContentBean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val meViewModel = hiltViewModel<MeViewModel>()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var selectedIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val isSearchMode = remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(meViewModel.shareItemFlow) {
        meViewModel.shareItemFlow.collect {
            val result = snackBarHostState.showSnackbar("确认分享给${it.userName}吗？", actionLabel = "确认", withDismissAction = true)
            Log.v("Loren", "result = $result")
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    snackBarHostState.showSnackbar("分享成功")
                }

                SnackbarResult.Dismissed -> {
                    snackBarHostState.showSnackbar("分享取消")
                }
            }
        }
    }

    val state = rememberNestedScrollMeState(coroutineScope)
    MeDrawer(drawerState) {
        NestedScrollMe(
            coroutineScope = coroutineScope,
            state = state,
            selectedIndex = selectedIndex,
            updateSelected = {
                if (it != selectedIndex) {
                    selectedIndex = it
                }
            },
            isSearchMode = isSearchMode.value,
            updateSearchMode = {
                isSearchMode.value = it
            },
            clickMenu = {
                coroutineScope.launch {
                    drawerState.open()
                }
            },
            clickShare = {
                showBottomSheet = true
            },
            navigateToContentDetail = navigateToContentDetail
        )

        if (showBottomSheet) {
            var enabled by remember {
                mutableStateOf(false)
            }
            LaunchedEffect(Unit) {
                enabled = true
            }
            ModalBottomSheet(
                windowInsets = WindowInsets(0, 0, 0, 0),
                containerColor = RedBookTheme.colors.background,
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                onDismissRequest = {
                    showBottomSheet = false
                },
                dragHandle = null,
                sheetState = sheetState
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "分享至",
                        style = RedBookTheme.textStyle.bodySmall,
                        color = RedBookTheme.colors.body
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                    ) {
                        itemsIndexed(items = MockDataModule.provideUsers(), key = { index, item ->
                            index
                        }) { index, item ->
                            val offsetY by animateFloatAsState(
                                targetValue = if (enabled) 0f else 500f,
                                spring(0.45f, (500f - index * 50).coerceAtLeast(0f)),
                                label = "shareHeadImgAnimate"
                            )
                            AsyncImage(
                                model = item.headImg,
                                contentDescription = "shareHeadImg",
                                modifier = Modifier
                                    .graphicsLayer {
                                        translationY = offsetY
                                    }
                                    .padding(horizontal = 12.dp)
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(32.dp))
                                    .border(1.dp, RedBookTheme.colors.divider, RoundedCornerShape(32.dp))
                                    .cancelRipperClick {
                                        meViewModel.shareItem(item)
                                        coroutineScope
                                            .launch {
                                                sheetState.hide()
                                            }
                                            .invokeOnCompletion {
                                                if (!sheetState.isVisible) {
                                                    showBottomSheet = false
                                                }
                                            }
                                    }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun MeDrawer(drawerState: DrawerState, content: @Composable () -> Unit) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(200.dp),
                drawerShape = RoundedCornerShape(0.dp),
                drawerContainerColor = RedBookTheme.colors.background
            ) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    for (index in 0 until 10) {
                        Text(
                            "Drawer title $index", modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp, 16.dp), style = RedBookTheme.textStyle.titleSmall,
                            color = RedBookTheme.colors.title
                        )

                    }
                }
            }
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        content()
    }
}

@Composable
private fun RefreshContent(
    topBarHeight: Int,
    randomColor: Color,
    coroutineScope: CoroutineScope,
    state: NestedScrollMeState,
    selectedIndex: Int,
    updateSelected: (Int) -> Unit,
    isSearchMode: Boolean,
    updateSearchMode: (Boolean) -> Unit,
    navigateToContentDetail: (ContentBean) -> Unit
) {

    val refreshState = rememberCommonRefreshState(coroutineScope)

    CommonRefresh(
        state = refreshState,
        onRefresh = {

        },
        headerIndicator = {
            CommonRefreshHeader(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(28.dp)
                    .graphicsLayer {
                        translationY = state.topBarHeight.toFloat()
                    },
                state = refreshState
            )
        }
    ) {
        SubcomposeLayout(
            modifier = Modifier
                .fillMaxSize()
        ) { constraints ->
            val functionBar = subcompose("functionBar") {
                MeFunctionBar(
                    state = state,
                    coroutineScope = coroutineScope,
                    selectedIndex = selectedIndex,
                    onTabClick = updateSelected,
                    isSearchMode = isSearchMode,
                    onSearchClick = updateSearchMode
                )
            }.first().measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
            state.functionBarHeight = functionBar.height

            val contentBar = subcompose("contentBar") {
                MeTabContent(
                    state,
                    randomColor,
                    with(LocalDensity.current) { topBarHeight.toDp() },
                    with(LocalDensity.current) { functionBar.height.toDp() }
                )
            }.first().measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
            state.contentBarHeight = contentBar.height

            val backgroundImage = subcompose("backgroundImage") {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationY = -refreshState.indicatorOffset
                        },
                    model = R.drawable.pic_vitality,
                    contentDescription = "background",
                    contentScale = ContentScale.Crop,
                )
            }.first().measure(
                constraints.copy(
                    minHeight = (state.contentBarHeight + refreshState.indicatorOffset.coerceAtLeast(0f)).toInt(),
                    maxHeight = Constraints.Infinity
                )
            )

            val viewPager = subcompose("viewPager") {
                MeViewPager(state, refreshState, isSearchMode, selectedIndex, updateSelected, navigateToContentDetail)
            }.first().measure(constraints.copy(minHeight = 0, maxHeight = constraints.maxHeight - topBarHeight - functionBar.height))

            layout(constraints.maxWidth, constraints.maxHeight) {
                backgroundImage.placeRelative(0, state.offset.toInt().coerceAtMost(0))
                contentBar.placeRelative(0, state.offset.toInt())
                functionBar.placeRelative(0, contentBar.height - functionBar.height + state.offset.toInt())
                viewPager.placeRelative(0, contentBar.height + state.offset.toInt())
            }
        }
    }

}


@Composable
private fun NestedScrollMe(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    state: NestedScrollMeState,
    selectedIndex: Int = 0,
    updateSelected: (Int) -> Unit = {},
    isSearchMode: Boolean = false,
    updateSearchMode: (Boolean) -> Unit = {},
    clickMenu: () -> Unit = {},
    clickShare: () -> Unit = {},
    navigateToContentDetail: (ContentBean) -> Unit
) {
    val randomColor = rememberSaveable(state, saver = ColorSaver) {
        Color(Random.nextInt(0, 100), Random.nextInt(0, 100), Random.nextInt(0, 100), 255)
    }

    SubcomposeLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(RedBookTheme.colors.background)
    ) { constraints ->
        val topBar = subcompose("topBar") {
            MeTopBar(state, randomColor, isSearchMode, clickMenu, clickShare)
        }.first().measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
        state.topBarHeight = topBar.height
        val refreshContent = subcompose("refreshContent") {
            RefreshContent(topBar.height, randomColor, coroutineScope, state, selectedIndex, updateSelected, isSearchMode, updateSearchMode, navigateToContentDetail)
        }.first().measure(constraints.copy(minHeight = 0, maxHeight = constraints.maxHeight))
        layout(constraints.maxWidth, constraints.maxHeight) {
            refreshContent.placeRelative(0, 0)
            topBar.placeRelative(0, 0)
        }
    }
}


@Composable
private fun MeTopBar(
    state: NestedScrollMeState,
    randomColor: Color,
    isSearchMode: Boolean,
    clickMenu: () -> Unit,
    clickShare: () -> Unit,
) {
    val fraction = remember {
        mutableFloatStateOf(0f)
    }
    val headImgBottom = with(LocalDensity.current) {
        96.dp.toPx()
    }
    LaunchedEffect(state.offset) {
        // 滚动到头像底部即不透明 到头像底部需要滚动16.dp+80.dp
        fraction.floatValue = (-state.offset / headImgBottom).coerceIn(0f, 1f)
    }

    val dynamicColor = remember {
        derivedStateOf {
            randomColor.copy(alpha = fraction.floatValue)
        }
    }
    val offsetY by animateFloatAsState(
        targetValue = if (fraction.floatValue == 1f) 0f else with(LocalDensity.current) { 50.dp.toPx() },
        label = "SmallHeadImgAnimate"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(dynamicColor.value)
            .windowInsetsPadding(WindowInsets.statusBars)
            .clipToBounds(),
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .alpha(if (isSearchMode) 0f else 1f),
            onClick = {
                clickMenu()
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White
            )
        }
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .alpha(if (isSearchMode) 0f else 1f),
            onClick = {
                clickShare()
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Share, contentDescription = "Share", tint = Color.White
            )
        }
        AsyncImage(
            model = R.drawable.icon_hanbao,
            contentDescription = "smallHeadImg",
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    translationY = offsetY
                }
                .size(24.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, Color.White, RoundedCornerShape(24.dp))
        )
    }
}

@Composable
private fun MeTabContent(state: NestedScrollMeState, randomColor: Color, topSpace: Dp, bottomSpace: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .scrollable(
                state = state.scrollState,
                orientation = Orientation.Vertical
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(0f to Color.Transparent, 0.7f to randomColor))
        )
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp + topSpace, start = 16.dp, end = 16.dp, bottom = 16.dp + bottomSpace)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    AsyncImage(
                        model = R.drawable.icon_hanbao,
                        contentDescription = "headImg",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(80.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(80.dp))
                    )
                    Icon(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .background(Color.Black, RoundedCornerShape(24.dp)),
                        imageVector = Icons.Filled.AddCircle,
                        tint = Color(0xFFFFEB3B),
                        contentDescription = "Share"
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(text = "Loren", color = Color.White, style = RedBookTheme.textStyle.titleLarge)
                    Text(text = "小红书号：123456789", color = Color.LightGray, style = RedBookTheme.textStyle.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "小白日常分享\n📷 Nikon Z30\n📷 Sony A7C2、ZV-E10", color = Color.White, style = RedBookTheme.textStyle.bodySmall)

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                TextAndNumItem(13, "关注")
                Spacer(modifier = Modifier.width(16.dp))
                TextAndNumItem(18, "粉丝")
                Spacer(modifier = Modifier.width(16.dp))
                TextAndNumItem(342, "获赞与收藏")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MeFunctionBar(
    state: NestedScrollMeState,
    coroutineScope: CoroutineScope,
    selectedIndex: Int,
    onTabClick: (Int) -> Unit,
    isSearchMode: Boolean,
    onSearchClick: (Boolean) -> Unit
) {
    val userInput = remember {
        mutableStateOf("")
    }
    val width = LocalView.current.width
    val focusManager = LocalFocusManager.current
    val focusRequester = remember {
        FocusRequester()
    }
    val imeVisible = WindowInsets.isImeVisible
    LaunchedEffect(imeVisible) {
        if (!imeVisible) {
            onSearchClick(false)
            focusManager.clearFocus()
        }
    }

    val searchAnimate by animateFloatAsState(targetValue = if (isSearchMode) 1f else 0f, label = "SearchAnimate")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(RedBookTheme.colors.background, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .scrollable(
                state = state.scrollState,
                orientation = Orientation.Vertical
            )
    ) {

        RedBookTabRow(
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.Center),
            selectedIndex = selectedIndex,
            indicator = {
                Box(
                    modifier = Modifier
                        .size(20.dp, 3.dp)
                        .background(RedBookTheme.colors.theme, RoundedCornerShape(3.dp))
                )

            }
        ) {
            stringArrayResource(id = com.loren.redbook.me.R.array.me_tab).forEachIndexed { index, s ->
                Box(
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .padding(horizontal = 16.dp)
                        .cancelRipperClick {
                            onTabClick(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = s,
                        style = RedBookTheme.textStyle.titleSmall,
                        color = if (selectedIndex == index) RedBookTheme.colors.title else RedBookTheme.colors.body
                    )
                }
            }
        }

        Icon(
            modifier = Modifier
                .cancelRipperClick {
                    onSearchClick(true)
                    focusRequester.requestFocus()
                    coroutineScope.launch {
                        state.scrollToFold()
                    }
                }
                .align(Alignment.CenterEnd)
                .size(40.dp)
                .padding(10.dp),
            imageVector = Icons.Filled.Search,
            contentDescription = "Search",
            tint = RedBookTheme.colors.body
        )

        Row(
            modifier = Modifier
                .graphicsLayer {
                    alpha = searchAnimate
                    translationX = width - width * searchAnimate
                }
                .fillMaxSize()
                .background(RedBookTheme.colors.background, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .height(32.dp)
                    .weight(1f)
                    .background(RedBookTheme.colors.window, RoundedCornerShape(32.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(20.dp),
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = RedBookTheme.colors.body
                )
                BasicTextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged {

                        },
                    value = userInput.value,
                    cursorBrush = SolidColor(RedBookTheme.colors.theme),
                    textStyle = RedBookTheme.textStyle.bodySmall,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    onValueChange = {

                    }
                ) { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (userInput.value.isBlank()) {
                            Text(text = "搜索我的笔记/收藏/赞过", style = RedBookTheme.textStyle.bodySmall, color = RedBookTheme.colors.body)
                        }
                        innerTextField()
                    }
                }

            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                modifier = Modifier.cancelRipperClick {
                    onSearchClick(false)
                    focusManager.clearFocus()
                },
                text = "取消",
                style = RedBookTheme.textStyle.titleSmall,
                color = RedBookTheme.colors.body
            )
        }


    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MeViewPager(
    state: NestedScrollMeState,
    refreshState: CommonRefreshState,
    isSearchMode: Boolean,
    selectedIndex: Int,
    pageScroll: (Int) -> Unit,
    navigateToContentDetail: (ContentBean) -> Unit
) {
    val pageCount = stringArrayResource(id = com.loren.redbook.me.R.array.me_tab).size
    val pagerState = rememberPagerState(initialPage = 0) {
        pageCount
    }
    LaunchedEffect(selectedIndex) {
        if (pagerState.targetPage != selectedIndex) {
            pagerState.animateScrollToPage(selectedIndex)
        }
    }
    LaunchedEffect(pagerState.targetPage) {
        snapshotFlow {
            pagerState.targetPage
        }.collect {
            pageScroll(it)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .alpha(if (isSearchMode) 0f else 1f)
            .pointerInput(isSearchMode) {
                if (isSearchMode) {
                    awaitEachGesture {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            event.changes.forEach { it.consume() }
                        }
                    }
                }
            }
    ) {

        when (it) {
            0 -> {
                MeNoteComponent(state, refreshState, navigateToContentDetail)
            }

            else -> {
                LaunchedEffect(refreshState.isRefreshing) {
                    if (refreshState.isRefreshing) {
                        delay(1000)
                        refreshState.isRefreshing = false
                    }
                }
                Text(
                    text = "Me $it",
                    modifier = Modifier
                        .fillMaxSize()
                        .scrollable(state.scrollState, Orientation.Vertical),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}

@Composable
private fun TextAndNumItem(num: Int, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "$num", color = Color.White, style = RedBookTheme.textStyle.bodySmall, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = text, color = Color.White, style = RedBookTheme.textStyle.bodySmall)
    }
}

@Composable
private fun MeNoteComponent(state: NestedScrollMeState, refreshState: CommonRefreshState, navigateToContentDetail: (ContentBean) -> Unit) {
    val meViewModel = hiltViewModel<MeViewModel>()
    val commonPaging = meViewModel.commonPagingFlow.collectAsLazyPagingItems()
    val staggeredGridState = rememberLazyStaggeredGridState()

    LaunchedEffect(refreshState.isRefreshing) {
        if (refreshState.isRefreshing) {
            commonPaging.refresh()
        }
    }

    LaunchedEffect(commonPaging.loadState.refresh) {
        when {
            commonPaging.loadState.refresh is LoadState.NotLoading -> {
                refreshState.isRefreshing = false
            }

            commonPaging.loadState.refresh is LoadState.Error -> {
                refreshState.isRefreshing = false
            }
        }
    }

    LazyVerticalStaggeredGridWithPaging(
        lazyPagingItems = commonPaging,
        state = staggeredGridState,
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalItemSpacing = 4.dp,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(state.connection)
    ) { bean, index ->
        ContentItem(contentBean = bean, navigateToDetail = navigateToContentDetail)
    }

}

@Preview(showBackground = true)
@Composable
private fun ForegroundColor() {
    val randomColor = remember {
        Color(
            Random.nextInt(0, 100),
            Random.nextInt(0, 100),
            Random.nextInt(0, 100), 255
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(
                Brush.verticalGradient(
                    0f to Color.Transparent, 0.7f to randomColor
                )
            )
    )
}