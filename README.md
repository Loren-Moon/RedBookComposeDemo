### 介绍

使用compose仿写的简单版小红书APP，使用的技术包括VersionCatalog、Navigation、Paging3、自定义Layout、NestedScrollConnection、compose共享元素、EdgeToEdge、自定义主题

### 效果图

| ![Screen\_recording\_20240823\_144803.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/11678e07af574fcc825cad5ad94e1ee2~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=fSjCkLSx%2BWH5bDctodq9jgsVyHc%3D) | ![Screen\_recording\_20240823\_144905.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/3312581f0dc9469a841e1886d5c37d87~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=UfFgpVoZK9TKnnLAIbqD%2BuM7yeA%3D) | ![Screen\_recording\_20240823\_145010.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/b819a7640a184244a3ccfea8fc4c58c0~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=EVeVi7Kk2bxHUSen11SJvV1KygE%3D) |
| --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |

### 版本管理

版本管理使用了VersionCatalog，可以参考官方项目[nowinandroid](https://github.com/android/nowinandroid)、[文章一](https://juejin.cn/post/6997396071055900680?searchId=202408221052359FA455835FD58856F2C1)、[文章二](https://juejin.cn/post/7339024907717804095?searchId=202408221052359FA455835FD58856F2C1)

![image.png](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/56b87f7c22bb4c1cbd7c696cdc5a86ce~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=uqzr1fYrxz%2FEnwK0iFJO6uz4qKk%3D)

### 路由导航

使用Navigation进行导航，图例如下

![image.png](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/b02b7c39baac487bb843dfc490707927~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=b9TS53B4CoywZSprbXVNDKQv49k%3D)

这里在`AppNavHost`中有两个导航页，一个是`main`，一个是`detail`，`main`里面嵌套了一个`MainNavHost`导航。

之所以这样涉及是由于`MainNavHost`导航的大小不包括底部BottomBar，如果`detail`页面写在这个导航，就会导致打开详情页页面大小无法撑满屏幕，所以使用嵌套导航的结构

```kotlin
class MainActivity {
  setContent {
    AppNavHost()
  }
}

fun AppNavHost() {
  composable("main") {
    Scaffold(
        bottomBar = {
          BottomBar()
        }
    ) { paddingValues -> // 这里的原因导致MainNavHost的高度是去除了bottomBar
      Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        MainNavHost()
      }
    }
  }
  composable("home_detail")
}

fun MainNavHost() {
  composable("home")
  composable("shopping")
  composable("message")
  composable("me")
}
```

### 首页结构

![image.png](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/829357ae17c74d279f56367234b70c92~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=%2Fv5brUgYyshnc9HA94pSKgU67xo%3D)

#### 一、自定义ScrollableTabRow

TabRow1+TabRow2我们仿写官方的`ScrollableTabRow`自定义一个`RedBookTabRow`，之所以自定义是因为官方的`ScrollableTabRow`有个默认的最小宽度`90.dp`，并且无法自定义，而横向滚动tabRow我们希望tab的宽度是根据每个tab内容宽度自适应的。
![image.png](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/68fdfe5d7196450a90ee64628de2b62c~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=LSeI7531feag2FXsoz5saLabSAg%3D)
![image.png](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/4dedb4b31b5a4ab3a4276886e939d5bf~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=W%2BSD2wCKyt0Rli9Vr6ETxzuMa38%3D)
修改如下
![image.png](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/400bf8e6910a4c2490ddf1bbf2130bb7~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=tmd%2Bl8q1UFPcPyV4EAreUqhlgmk%3D)

#### 二、HorizontalPage左右滚动优先级

当`TabRow1`与`TabRow2`都显示的时候，左右切换优先处理内部的HorizontalPager2，当`TabRow2`隐藏的时候，希望优先处理外部的HorizontalPager1

```kotlin
// 使用userScrollEnabled简单处理即可，隐藏直接userScrollEnabled=false
HorizontalPager(
    state = pagerState,
    modifier = Modifier.fillMaxSize(),
    userScrollEnabled = animateHeaderState.flag,
)
```

#### 三、下拉刷新&上拉加载\&Paging3数据渲染

下拉刷新我们自定义了一个控件`CommonRefresh`，配合`Paging3`，完成了列表内容的滚动加载。

**下拉刷新控件**

这里通过自定义`Layout`+`NestedScrollConnection`实现

具体思路可以参考这篇[文章](https://juejin.cn/post/7113733273561333797)中的下拉刷新，我们基于这个修改一下

<details>
<summary>点击查看完整代码</summary>

<pre><code>
@Composable
fun CommonRefresh(
    modifier: Modifier = Modifier,
    state: CommonRefreshState,
    onRefresh: (suspend () -> Unit)? = null,
    headerIndicator: (@Composable () -> Unit)? = {
        CommonRefreshHeader(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(28.dp),
            state = state
        )
    },
    content: @Composable () -> Unit
) {
    LaunchedEffect(state.isRefreshing) {
        if (state.isRefreshing) {
            state.animateIsOver = false
            onRefresh?.invoke()
        } else {
            state.animateOffsetTo(0f)
        }
    }

    Layout(
        modifier = modifier.nestedScroll(state.connection),
        content = {
            content()
            headerIndicator?.invoke()
        }
    ) { measurables, constraints ->
        val contentPlaceable = measurables[0].measure(constraints)
        val headerPlaceable = measurables.getOrNull(1)?.measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
        state.headerHeight = headerPlaceable?.height?.toFloat() ?: 0f
        layout(constraints.maxWidth, constraints.maxHeight) {
            contentPlaceable.placeRelative(0, state.indicatorOffset.roundToInt())
            headerPlaceable?.placeRelative(0, -headerPlaceable.height + state.indicatorOffset.roundToInt())
        }
    }
}

@Composable
fun rememberCommonRefreshState(
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): CommonRefreshState {
    return remember {
        CommonRefreshState(coroutineScope)
    }
}

@Stable
class CommonRefreshState(private val coroutineScope: CoroutineScope) {
    var headerHeight = 0f
    var enableRefresh = true
    var isRefreshing by mutableStateOf(false)
    var animateIsOver by mutableStateOf(true)
    private val _indicatorOffset = Animatable(0f)
    private val mutatorMutex = MutatorMutex()

    val indicatorOffset: Float
        get() = _indicatorOffset.value

    fun isLoading() = !animateIsOver || isRefreshing

    suspend fun animateOffsetTo(offset: Float, durationMillis: Int = AnimationConstants.DefaultDurationMillis) {
        mutatorMutex.mutate {
            _indicatorOffset.animateTo(offset, animationSpec = tween(durationMillis)) {
                if (this.value == 0f) {
                    animateIsOver = true
                }
            }
        }
    }

    suspend fun snapOffsetTo(offset: Float) {
        mutatorMutex.mutate(MutatePriority.UserInput) {
            _indicatorOffset.snapTo(offset)
        }
    }

    private fun consumed(needConsumedY: Float) {
        if (needConsumedY == 0f) return
        coroutineScope.launch {
            snapOffsetTo(indicatorOffset + needConsumedY)
        }
    }


    internal val connection = object : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            return when {
                enableRefresh && available.y &lt; 0 -> {
                    val canConsumed = (available.y * 0.5f).coerceAtLeast(0 - indicatorOffset)
                    consumed(canConsumed)
                    available.copy(x = 0f, y = canConsumed / 0.5f)
                }

                else -> Offset.Zero
            }
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            return when {
                enableRefresh && available.y > 0 -> {
                    val canConsumed = available.y * 0.5f
                    consumed(canConsumed)

                    if (source == NestedScrollSource.Fling && indicatorOffset > headerHeight) {
                        throw CancellationException()
                    }

                    available.copy(x = 0f, y = canConsumed / 0.5f)
                }

                else -> Offset.Zero
            }
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            if (indicatorOffset >= headerHeight) {
                if (!isLoading()) {
                    isRefreshing = true
                    animateOffsetTo(headerHeight)
                    return available
                }
            }

            return super.onPreFling(available)
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            if (indicatorOffset > 0) {
                if (isRefreshing && indicatorOffset > headerHeight) {
                    animateOffsetTo(headerHeight)
                } else if (!isRefreshing) {
                    animateOffsetTo(0f)
                }
                return available
            }

            return super.onPostFling(consumed, available)
        }

    }

}

@Composable
fun CommonRefreshHeader(modifier: Modifier = Modifier, state: CommonRefreshState) {
    if (state.isRefreshing) {
        CircleLoading(modifier = modifier)
    } else {
        val progress = (state.indicatorOffset.coerceAtLeast(0f) / state.headerHeight.coerceAtLeast(1f)).coerceAtMost(1f)
        val rotate = 0f
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            CircleProgress(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotate),
                progress = progress
            )
        }
    }
}
</code></pre>

</details>

注意点：我们希望快速下拉的时候如果header不要一直往下滚动，，也不是僵硬的到某个高度直接折叠，而是header已经完全展示后就马上折叠起来

```kotlin
onPostScroll() {
    if (source == NestedScrollSource.Fling && indicatorOffset > headerHeight) {
        throw CancellationException()
    }
}
```

![Screen\_recording\_20240823\_184043 2.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/8e2b3786008d49738e3dfb33579f3189~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=gdTyVdszGSOWcbqgdNqdbXyqsu8%3D)

**Paging3使用步骤**

1.  定义`PagingSource`

```kotlin
class CommonPagingSource(
    private val pictures: List<Int>,
    private val mockUsers: List<UserBean>,
    private val isVideo: Boolean = false,
    private val mockPages: Int = 3,
) : PagingSource<Int, ContentBean>() {
    override fun getRefreshKey(state: PagingState<Int, ContentBean>): Int? {
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ContentBean> {
        return try {
            val page = params.key ?: 1
            val data = requestData(page, params.loadSize) ?: emptyList()
            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun requestData(page: Int, pageSize: Int): List<ContentBean>? {
        if (page > mockPages) {
            return null
        } else {
            val list = mutableListOf<ContentBean>()
            pictures.forEachIndexed { index, pic ->
                val user = mockUsers[index]
                list.add(
                    ContentBean(
                        id = Random.nextLong(),
                        pic = pic,
                        title = if (isVideo) "视频：${user.userName}" else "我是${user.userName}",
                        user = user,
                        likeNum = Random.nextInt(0, 20000),
                        isVideo = if (isVideo) true else Random.nextBoolean()
                    )
                )
            }
            delay(2000)
            Log.v("Loren", "加载数据page=$page size=$pageSize")
            return list.shuffled().takeLast(pageSize)
        }
    }
}
```

2.  创建PagingData流

```kotlin
class CommonContentRepository @Inject constructor(
    @Dispatcher(MyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @MockDataModule.MockContentPic private val pictures: List<Int>,
    private val mockUsers: List<UserBean>
) {

    val commonPagingFlow = Pager(
        config = PagingConfig(
            pageSize = 20, // 每次加载20条
            initialLoadSize = 20, // 首次默认加载20条
            prefetchDistance = 4 // 滚动到离当前显示项还有4条时开始预取数据
        ),
        pagingSourceFactory = { CommonPagingSource(pictures, mockUsers, isVideo = false, mockPages = 3) }
    ).flow.flowOn(ioDispatcher)

}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val commonContentRepository: CommonContentRepository
) : ViewModel() {
    val commonPagingFlow = commonContentRepository.commonPagingFlow.cachedIn(viewModelScope)
}
```

3.  根据数据流展示UI

```kotlin
// 首次加载展示loading
if (lazyPagingItems.loadState.refresh is LoadState.Loading && lazyPagingItems.itemCount == 0) {
    CircleLoading(
        modifier = Modifier.fillMaxSize()
    )
}
// 加载更多时展示loading
pagingItems.loadState.append is LoadState.Loading -> {
    item(span = StaggeredGridItemSpan.FullLine) {
        CircleLoading()
    }
}
// 没有更多数据了
pagingItems.loadState.append == LoadState.NotLoading(true) -> {
    // 没有数据了
    item(span = StaggeredGridItemSpan.FullLine) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "没有数据了", style = RedBookTheme.textStyle.bodySmall, color = RedBookTheme.colors.body)
        }
    }
}
```

4.  下拉刷新触发paging的refresh

```kotlin
val homePagingItems = homeViewModel.commonPagingFlow.collectAsLazyPagingItems()

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
) {}
```

#### 四、自定义CircleProgress

![image.png](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/9ff32e324fbe458f8c5913353e7deaa8~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=6mGZr7KHUoPSS1UaDAZC7EtyC7w%3D)
![Screen\_recording\_20240823\_201620.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/e1de87c24b5d46f6a8aa3a5195ce5173~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=05iQxLILuW62dWG4raKywbWaXnQ%3D)

```kotlin
@Composable
fun CircleProgress(
    modifier: Modifier = Modifier,
    color: Color = Color.LightGray,
    progress: Float = 1f
) {
    Canvas(modifier = modifier.drawWithCache {
        val ringStrokeWidth = 3.dp.toPx()
        val radius = min(size.width, size.height) / 2 - ringStrokeWidth
        val topTopLeft = Offset(size.width / 2 - radius, size.height / 2 - radius)
        // 两个半圆弧形中间有一点间隔 取一个圆弧140度 左右各减少20度
        onDrawWithContent {
            val sweepAngle = 140f * progress
            // 绘制上半圆
            drawArc(
                color = color,
                startAngle = -160f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topTopLeft,
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = ringStrokeWidth, cap = StrokeCap.Round)
            )
            // 绘制下半圆
            drawArc(
                color = color,
                startAngle = 20f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topTopLeft,
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = ringStrokeWidth, cap = StrokeCap.Round)
            )
        }
    }) {}
}
```

### 我的页面结构

<img src="https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/d45c5703dcf74589bcf6a0902cf82db3~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=oXL39icBDmFwmm8XalasR3f1xjw%3D" width="60%">

```kotlin
// 大致布局 实际通过自定义Layout布局
Drawer {
    Box {
        RefreshContent()
        TopBar()
    }
}

RefreshContent() {
    Column {
        Box {
            BackgroundImg()
            Column {
                MeTabContent()
                MeFunctionBar()
            }
        }
        MeViewPager()
    }
}
```

**页面的协调滚动分析**

| 向上滚动                                                                                                                                                                                                                                                                                                                                                                                   | 向下滚动                                                                                                                                                                                                                                                                                                                                                                                 | 非列表区域的滚动                                                                                                                                                                                                                                                                                                                                                                              |
| -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ![up.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/caea7fa97bba47ceace2c503e4a8d6d2~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=DEeKvNr0bQgXvboBCzNs8ER9ltw%3D) | ![down.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/ecf273bc58f841a383b9a0ab02618227~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=Vfp8XKDxGzQcexrKp3quqCFy8Sk%3D) | ![ddd.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/87a64db9cee0478d82e366649c11e9b2~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=a7yQJHlT1tz23Xq14l3MTARx7JU%3D) |
| `TopBar`透明度改变 0-\>1<br/>`MeTabContent`折叠，折叠高度=整个顶部内容高度-TopBar高度-FunctionBar高度<br/>`MeFunctionBar`吸顶                                                                                                                                                                                                                                                                                    | `TopBar`透明度改变 1-\>0<br/>`MeViewPager`内部的滚动布局滚动到顶部时`MeTabContent`展开<br/>可展开高度=整个顶部内容高度-TopBar高度-FunctionBar高度，到顶时触发刷新<br/>`MeFunctionBar`吸顶                                                                                                                                                                                                                                           | 手指在顶部区域也能拖拽，下拉刷新，上拉最多只能折叠`MeTabContent`                                                                                                                                                                                                                                                                                                                                               |

通过上面的三种情况分析，首先我们需要分别测量出`TopBar`、`MeTabContent`、`MeFunctionBar`对应的高度用于滚动计算。

创建一个类管理协调滚动、记录控件高度以及记录偏移量

```kotlin
// 部分代码片段
class NestedScrollMeState {
    var topBarHeight = 0
    var contentBarHeight = 0
    var functionBarHeight = 0
    private val _offset = Animatable(0f)
    val offset: Float // 布局滚动的偏移量
        get() = _offset.value
}

@Composable
fun rememberNestedScrollMeState() = remember {
    NestedScrollMeState()
}
```

稍微布局一下

```kotlin
// 部分代码片段
val state = rememberNestedScrollMeState(）
SubcomposeLayout(
    modifier = Modifier.fillMaxSize()
) { constraints ->
    val topBar = subcompose("topBar") {
        MeTopBar()
    }.first().measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
    state.topBarHeight = topBar.height // 记录高度
    val refreshContent = subcompose("refreshContent") {
        RefreshContent()
    }.first().measure(constraints.copy(minHeight = 0, maxHeight = constraints.maxHeight))
    layout(constraints.maxWidth, constraints.maxHeight) {
        refreshContent.placeRelative(0, 0)
        topBar.placeRelative(0, 0)
    }
}
```

```kotlin
SubcomposeLayout(
    modifier = Modifier.fillMaxSize()
) { constraints ->
    val functionBar = subcompose("functionBar") {
        MeFunctionBar()
    }.first().measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
    state.functionBarHeight = functionBar.height // 记录高度

    val contentBar = subcompose("contentBar") {
        MeTabContent()
    }.first().measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
    state.contentBarHeight = contentBar.height // 记录高度

    val backgroundImage = subcompose("backgroundImage") {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            model = R.drawable.pic_vitality,
            contentDescription = "background",
            contentScale = ContentScale.Crop,
        )
    }.first().measure(
        constraints.copy(
            minHeight = state.contentBarHeight, // 图片撑满顶部内容布局
            maxHeight = Constraints.Infinity
        )
    )

    val viewPager = subcompose("viewPager") {
        MeViewPager()
    }.first().measure(
        constraints.copy(
            minHeight = 0, 
            maxHeight = constraints.maxHeight - topBarHeight - functionBar.height // 底部内容区域=总高度-topBarHeight-functionBarHeight
        )
    ) 

    layout(constraints.maxWidth, constraints.maxHeight) {
        backgroundImage.placeRelative(0, 0)
        contentBar.placeRelative(0, 0)
        functionBar.placeRelative(0, contentBar.height - functionBar.height)
        viewPager.placeRelative(0, contentBar.height)
    }
}
```

这个时候我们的布局是不会根据偏移量而滚动的，然后我们需要让上面的布局根据滚动偏移量做文章

#### 一、MeTabContent前景渐变色

![image.png](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/f8e210df43a246a6ba1bde71851759a0~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=Gq9YQkhvbsLGpEp7bwUCa0drgkc%3D)

#### 二、TopBar透明度变化及动画

![topbar.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/2a21bfb0ff824b56abc01f04061da55d~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=6kuyYOGGqAGi3WBmQ%2F9xrCRYNMQ%3D)

滚动到头像底部的时候，TopBar的透明度变为1，使用上面生成的那个随机颜色，并且做头像小动画。透明度变化`fraction=state.offset/滚动到头像底部的距离`，`backgroundColor=randomColor.copy(alpha = fraction)`

#### 三、嵌套滑动处理

*   手指向上滑动的情况，此时`available.y<0`，如果`functionBar`没有滚动到顶部，则预先劫持滑动事件，消费后再交由子布局，所以在`onPreScroll`中进行消费，`functionBar`吸顶后则不需要再消费
*   手指向下滑动的情况，此时`available.y>0`，在`onPostScroll`中进行消费，这里有可消费值代表底部数据已经滚到顶了，剩余的y事件交给这里处理，最多只能消费到offset为0的情况，再剩下没消费的y值传递给`父onPostScrol`，即下拉刷新控件

```kotlin
internal val connection = object : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val needConsumedY = when {
            available.y < 0 && offset > -(contentBarHeight - topBarHeight - functionBarHeight) -> {
                // drag up
                available.y.coerceAtLeast(-(contentBarHeight - topBarHeight - functionBarHeight) - offset)
            }

            else -> 0f
        }
        consumed(needConsumedY)
        return available.copy(x = 0f, y = needConsumedY)
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        val needConsumedY = when {
            available.y > 0 && offset < 0 -> {
                // drag down
                available.y.coerceAtMost(-offset)
            }

            else -> 0f
        }
        consumed(needConsumedY)
        return available.copy(x = 0f, y = needConsumedY)
    }
}

private fun consumed(needConsumedY: Float) {
    if (needConsumedY == 0f) return
    coroutineScope.launch {
        // 调整offset
        snapOffsetTo(offset + needConsumedY)
    }
}
```

**踩坑点**

一开始我的`HorizontalPager`版本是1.6.3，然后我把这个`connection`设置到最外层，发现当我快速下滑时，无法展开上面的内容
![Screen\_recording\_20240824\_222427.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/f3f7a8592636479b96bab95c2f385f15~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=DOiVVZUJ8c%2BIii6sOmrqo%2BDilsY%3D)

查看源码可知，HorizontalPager有一个`DefaultPagerNestedScrollConnection`<br/>
消费顺序：`父onPreScroll`->`子onPreScroll`->`子onPostScroll`->`父onPostScroll`<br/>
所以当我把我的connection设置到最外层，即`HorizontalPager`是子控件时，会优先响应`HorizontalPager`的`onPostScroll`，当我手指快速滑动时`throw CancellationException()`导致无法展开上面被折叠的内容，所以最后我把我的connection设置到我笔记页面的LazyVerticalStaggeredGrid上。
![image.png](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/d417ac09302f44ada454f4295bc2566f~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=7kTEQ3YxthcxXyG2KwOrWDqs1jU%3D)

The bug on [JetBrains/compose-multiplatform#4395](https://github.com/JetBrains/compose-multiplatform/issues/4395)

Fix on [Fix nested scroll when Pager involved in scrolling process](https://github.com/JetBrains/compose-multiplatform-core/pull/1154)

**PS：后续我升级物料清单`Compose 1.6.8`解决了这个问题**

![image.png](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/f2d2c304aa624c2d912af028ce456160~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=xkgIzoiRx4zzRqFAWMtiF7kv9GE%3D)

#### 四、TabContent区域的手势滚动

实现方式查看官方文档 [可滚动的修饰符](https://developer.android.google.cn/develop/ui/compose/touch-input/pointer-input/scroll?hl=zh-cn#scrollable-modifier)

通过 [`scrollable`](https://developer.android.google.cn/reference/kotlin/androidx/compose/foundation/gestures/package-summary?hl=zh-cn#\(androidx.compose.ui.Modifier\).scrollable\(androidx.compose.foundation.gestures.ScrollableState,androidx.compose.foundation.gestures.Orientation,kotlin.Boolean,kotlin.Boolean,androidx.compose.foundation.gestures.FlingBehavior,androidx.compose.foundation.interaction.MutableInteractionSource\)) 修饰符与滚动修饰符不同，`scrollable` 会检测滚动手势并捕获增量，但不会偏移其内容。系统会通过 [`ScrollableState`](https://developer.android.google.cn/reference/kotlin/androidx/compose/foundation/gestures/ScrollableState?hl=zh-cn)，此修饰符才能正常运行。

构建`ScrollableState`时，您必须提供`consumeScrollDelta`函数，该函数将在每个滚动步骤中调用（通过手势输入，平滑）滚动或快速滑动）。该函数必须返回滚动距离所消耗的量，以确保事件在存在具有`scrollable`的嵌套元素时传播修饰符。

我们构建一个`ScrollState`，返回的`needConsumedY`就是滚动所需要消耗的量，向下滚动时，可以消耗任意Y值，向上滚动时，我们希望吸顶后就不继续消耗Y值了。

```kotlin
val scrollState = ScrollableState {
    val needConsumedY = when {
        it > 0 && offset < 0 -> {
            // drag down
            it
        }

        it < 0 && offset > -(contentBarHeight - topBarHeight - functionBarHeight) -> {
            // drag up
            it.coerceAtLeast(-(contentBarHeight - topBarHeight - functionBarHeight) - offset)
        }

        else -> 0f
    }
    consumed(needConsumedY)
    needConsumedY
}

// 滚动值记录到offset
private fun consumed(needConsumedY: Float) {
    if (needConsumedY == 0f) return
    coroutineScope.launch {
        snapOffsetTo(offset + needConsumedY)
    }
}

fun MeTabContent() {
    Box(
        modifier = Modifier
            .scrollable(
                state = state.scrollState,
                orientation = Orientation.Vertical
            )
    )
}

```

#### 五、记录我的页面的滚动位置以及背景渐变色

切换bottomBar发现我的页面的滚动位置以及颜色状态都丢失了

| 没有Saver                                                                                                                                                                                                                                                                                                                                                                                   | 有Saver                                                                                                                                                                                                                                                                                                                                                                                |
| ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ![no\_saver.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/52cbfca6802d4e65bccb3ab8ac9088a8~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=3lZa9lkvu6Bbokyc%2FvVrTgPSoa0%3D) | ![saver.gif](https://p0-xtjj-private.juejin.cn/tos-cn-i-73owjymdk6/c46b3fedf1e042d89fa3c24ad6ccf6bc~tplv-73owjymdk6-jj-mark-v1:0:0:0:0:5o6Y6YeR5oqA5pyv56S-5Yy6IEAgTG9yZW4=:q75.awebp?policy=eyJ2bSI6MywidWlkIjoiODQ1MTgyMjczMTU5MSJ9&rk3s=f64ab15b&x-orig-authkey=f32326d3454f2ac7e96d3d06cdbb035152127018&x-orig-expires=1725197476&x-orig-sign=QegQk5luKYlULWazOQl%2BWqR7or8%3D) |

使用`rememberSaverable`，由于Color不是可序列化的数据结构，通过自定义Saver实现

```kotlin
val randomColor = rememberSaveable(state, saver = ColorSaver) {
    Color(Random.nextInt(0, 100), Random.nextInt(0, 100), Random.nextInt(0, 100), 255)
}

val ColorSaver = Saver<Color, Long>(
    save = { it.value.toLong() },
    restore = { Color(it.toULong()) }
)

@Composable
fun rememberNestedScrollMeState(coroutineScope: CoroutineScope) = rememberSaveable(saver = NestedScrollMeStateSaver(coroutineScope)) {
    NestedScrollMeState(coroutineScope)
}
class NestedScrollMeState(private val coroutineScope: CoroutineScope, offsetSave: Float = 0f) {
    private val _offset = Animatable(offsetSave)
}
/**
 * 保存滚动进度
 */
class NestedScrollMeStateSaver(private val coroutineScope: CoroutineScope) : Saver<NestedScrollMeState, Float> {
    override fun restore(value: Float): NestedScrollMeState {
        return NestedScrollMeState(coroutineScope, value)
    }

    override fun SaverScope.save(value: NestedScrollMeState): Float {
        return value.offset
    }
}
```

### 共享元素

官方文档 [导航间的共享元素动画](https://developer.android.google.cn/develop/ui/compose/animation/shared-elements/navigation?hl=zh-cn)

用`SharedTransitionLayout`包裹NavHost，用`CompostitionLocalProvider`将`SharedTransitionScope`以及`AnimatedVisibilityScope`向下传递

```kotlin
SharedTransitionLayout {
    CompositionLocalProvider(
        LocalNavHostSharedTransitionScope provides this
    ) {
        AppNavHost()
    }
}

fun NavGraphBuilder.composableWithCompositionLocal(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(route = route, arguments = arguments) {
        CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides this@composable
        ) {
            content(it)
        }
    }
}
```

### 主题&状态栏颜色

```kotlin
@Composable
fun RedBookTheme(themeType: AppThemeType = AppThemeType.Light, content: @Composable () -> Unit) {
    val colors =
        if (AppThemeType.isDark(themeType = themeType)) darkLorenColors else lightLorenColors

    CompositionLocalProvider(
        LocalCustomColors provides colors,
        LocalTextStyles provides RedBookTheme.textStyle
    ) {
        MaterialTheme(content = content)
    }
}

object RedBookTheme {
    val colors: LorenColors
        @Composable
        get() = LocalCustomColors.current
    val textStyle: LorenTextStyle
        @Composable
        get() = LocalTextStyles.current
}
```

我的页面状态栏图标颜色亮色，其他页面状态栏图标是暗色，使用`EdgeToEdge`控制

```kotlin
// statusBar图标颜色模式
// isDark=true，状态栏为浅色图标
val isDark = if (appState.iconIsLight && appState.currentDestination?.route == MAIN_ROUTE) true else AppThemeType.isDark(
    themeType = lorenTheme
)

DisposableEffect(lorenTheme, isDark) {
    enableEdgeToEdge(
        SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { isDark },
        SystemBarStyle.auto(Color.WHITE, Color.BLACK) { isDark },
    )
    onDispose { }
}
```

### 项目完整代码地址

🎉 [完整项目代码地址](https://github.com/Loren-Moon/RedBookComposeDemo)
[掘金文章地址](https://juejin.cn/spost/7406279925119074338)
