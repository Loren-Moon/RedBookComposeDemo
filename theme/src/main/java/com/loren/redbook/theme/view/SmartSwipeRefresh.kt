package com.loren.redbook.theme.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SmartSwipeRefresh(
    modifier: Modifier = Modifier,
    state: SmartSwipeRefreshState,
    onRefresh: (suspend () -> Unit)? = null,
    onLoadMore: (suspend () -> Unit)? = null,
    headerIndicator: @Composable (() -> Unit)? = null,
    footerIndicator: @Composable (() -> Unit)? = null,
    contentScrollState: ScrollableState? = null,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val connection = remember(coroutineScope) {
        SmartSwipeRefreshNestedScrollConnection(state, coroutineScope)
    }

    LaunchedEffect(state.refreshFlag) {
        when (state.refreshFlag) {
            SmartSwipeStateFlag.REFRESHING -> {
                state.animateIsOver = false
                onRefresh?.invoke()
            }

            SmartSwipeStateFlag.ERROR, SmartSwipeStateFlag.SUCCESS -> {
                delay(500)
                state.animateOffsetTo(0f)
            }

            else -> {}
        }
    }

    LaunchedEffect(state.loadMoreFlag) {
        when (state.loadMoreFlag) {
            SmartSwipeStateFlag.REFRESHING -> {
                state.animateIsOver = false
                onLoadMore?.invoke()
            }

            SmartSwipeStateFlag.ERROR, SmartSwipeStateFlag.SUCCESS -> {
                state.animateOffsetTo(0f, 50)
            }

            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        if (state.needFirstRefresh) {
            state.initRefresh()
        }
    }

    LaunchedEffect(state.indicatorOffset) {
        if (state.indicatorOffset < 0 && state.loadMoreFlag != SmartSwipeStateFlag.SUCCESS) {
            contentScrollState?.dispatchRawDelta(-state.indicatorOffset)
        }
    }

    Box(
        modifier = modifier.clipToBounds()
    ) {
        SubComposeSmartSwipeRefresh(
            headerIndicator = headerIndicator, footerIndicator = footerIndicator
        ) { header, footer ->
            state.headerHeight = header.toFloat()
            state.footerHeight = footer.toFloat()

            Box(modifier = Modifier.nestedScroll(connection)) {
                val p = with(LocalDensity.current) { state.indicatorOffset.toDp() }
                val contentModifier = when {
                    p > 0.dp -> Modifier.padding(top = p)
                    p < 0.dp && contentScrollState != null -> Modifier.padding(bottom = -p)
                    p < 0.dp -> Modifier.graphicsLayer { translationY = state.indicatorOffset }
                    else -> Modifier
                }
                Box(modifier = contentModifier) {
                    content()
                }
                headerIndicator?.let {
                    Box(modifier = Modifier
                        .align(Alignment.TopCenter)
                        .graphicsLayer { translationY = -header.toFloat() + state.indicatorOffset }) {
                        headerIndicator()
                    }
                }
                footerIndicator?.let {
                    Box(modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .graphicsLayer { translationY = footer.toFloat() + state.indicatorOffset }) {
                        footerIndicator()
                    }
                }
            }
        }
    }
}

enum class SmartSwipeStateFlag {
    IDLE, REFRESHING, SUCCESS, ERROR;
}

/**
 * 边界阈值策略
 * [ThresholdScrollStrategy.None] 阈值为0
 * [ThresholdScrollStrategy.UnLimited] 阈值为任意
 * [ThresholdScrollStrategy.Fixed] 阈值为固定数值
 */
sealed interface ThresholdScrollStrategy {
    data object None : ThresholdScrollStrategy

    data object UnLimited : ThresholdScrollStrategy

    data class Fixed(val height: Float) : ThresholdScrollStrategy
}

@Composable
fun rememberSmartSwipeRefreshState(): SmartSwipeRefreshState {
    return remember {
        SmartSwipeRefreshState()
    }
}

class SmartSwipeRefreshState {
    /**
     * 拖动粘性
     */
    var headerStickinessLevel = 0.5f
    var footerStickinessLevel = 1f

    /**
     * 头布局拖拽策略
     */
    var dragHeaderIndicatorStrategy: ThresholdScrollStrategy = ThresholdScrollStrategy.UnLimited

    /**
     * 尾布局拖拽策略
     */
    var dragFooterIndicatorStrategy: ThresholdScrollStrategy = ThresholdScrollStrategy.UnLimited

    /**
     * 头布局快速滑动策略
     */
    val flingHeaderIndicatorStrategy: ThresholdScrollStrategy
        get() = ThresholdScrollStrategy.Fixed(headerHeight)

    /**
     * 尾布局快速滑动策略
     */
    val flingFooterIndicatorStrategy: ThresholdScrollStrategy
        get() = ThresholdScrollStrategy.Fixed(footerHeight)

    /**
     * 首次进入页面是否触发刷新动画以及回调
     */
    var needFirstRefresh = false

    /**
     * 头布局测量高度
     */
    var headerHeight = 0f

    /**
     * 尾布局测量高度
     */
    var footerHeight = 0f

    /**
     * 是否开启刷新
     */
    var enableRefresh = true

    /**
     * 是否开启加载更多
     */
    var enableLoadMore = true

    // fling释放的时候header|footer是否有显示 显示则刷新 没显示动画回到原位
    var releaseIsEdge = false
    var refreshFlag by mutableStateOf(SmartSwipeStateFlag.IDLE)
    var loadMoreFlag by mutableStateOf(SmartSwipeStateFlag.IDLE)

    /**
     * 动画结束
     */
    var animateIsOver by mutableStateOf(true)
    private val _indicatorOffset = Animatable(0f)
    private val mutatorMutex = MutatorMutex()

    val indicatorOffset: Float
        get() = _indicatorOffset.value

    fun isLoading() = !animateIsOver || refreshFlag == SmartSwipeStateFlag.REFRESHING || loadMoreFlag == SmartSwipeStateFlag.REFRESHING

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

    suspend fun initRefresh() {
        snapOffsetTo(headerHeight)
        refreshFlag = SmartSwipeStateFlag.REFRESHING
    }

    fun strategyIndicatorHeight(strategy: ThresholdScrollStrategy): Float = when (strategy) {
        ThresholdScrollStrategy.None -> 0f
        is ThresholdScrollStrategy.Fixed -> strategy.height
        else -> Float.MAX_VALUE
    }
}

private class SmartSwipeRefreshNestedScrollConnection(
    val state: SmartSwipeRefreshState,
    private val coroutineScope: CoroutineScope
) : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        return when {
//            state.isLoading() -> Offset.Zero
            available.y < 0 && state.indicatorOffset > 0 -> {
                // header can drag [state.indicatorOffset, 0]
                val canConsumed = (available.y * state.headerStickinessLevel).coerceAtLeast(0 - state.indicatorOffset)
                scroll(canConsumed, true)
            }

            available.y > 0 && state.indicatorOffset < 0 -> {
                // footer can drag [state.indicatorOffset, 0]
                val canConsumed = (available.y * state.footerStickinessLevel).coerceAtMost(0 - state.indicatorOffset)
                scroll(canConsumed, false)
            }

            else -> Offset.Zero
        }
    }

    override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
        return when {
//            state.isLoading() -> Offset.Zero
            available.y > 0 && state.enableRefresh && state.headerHeight != 0f -> {
                val canConsumed = if (source == NestedScrollSource.Fling) {
                    (available.y * state.headerStickinessLevel).coerceAtMost(state.strategyIndicatorHeight(state.flingHeaderIndicatorStrategy) - state.indicatorOffset)
                        .coerceAtLeast(0f)
                } else {
                    (available.y * state.headerStickinessLevel).coerceAtMost(state.strategyIndicatorHeight(state.dragHeaderIndicatorStrategy) - state.indicatorOffset)
                        .coerceAtLeast(0f)
                }
                scroll(canConsumed, true)
            }

            available.y < 0 && state.enableLoadMore && state.footerHeight != 0f -> {
                val canConsumed = if (source == NestedScrollSource.Fling) {
                    (available.y * state.footerStickinessLevel).coerceAtLeast(-state.strategyIndicatorHeight(state.flingFooterIndicatorStrategy) - state.indicatorOffset)
                        .coerceAtMost(0f)
                } else {
                    (available.y * state.footerStickinessLevel).coerceAtLeast(-state.strategyIndicatorHeight(state.dragFooterIndicatorStrategy) - state.indicatorOffset)
                        .coerceAtMost(0f)
                }
                scroll(canConsumed, false)
            }

            else -> Offset.Zero
        }
    }

    private fun scroll(canConsumed: Float, isHeader: Boolean): Offset {
//        return if (canConsumed.absoluteValue > 0.5f) {
        coroutineScope.launch {
            state.snapOffsetTo(state.indicatorOffset + canConsumed)
        }

        if (!state.isLoading() && state.indicatorOffset + canConsumed < 0) {
            state.loadMoreFlag = SmartSwipeStateFlag.REFRESHING
        }

        return Offset(0f, canConsumed / (if (isHeader) state.headerStickinessLevel else state.footerStickinessLevel))
//        } else {
//            Offset.Zero
//        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        state.releaseIsEdge = state.indicatorOffset != 0f

        if (state.indicatorOffset >= state.headerHeight && state.releaseIsEdge) {
            if (!state.isLoading()) {
                state.refreshFlag = SmartSwipeStateFlag.REFRESHING
                state.animateOffsetTo(state.headerHeight)
                return available
            }
        }

        return super.onPreFling(available)
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        if (state.indicatorOffset > 0) {
            if (state.refreshFlag == SmartSwipeStateFlag.REFRESHING && state.indicatorOffset > state.headerHeight) {
                state.animateOffsetTo(state.headerHeight, 600)
            } else if (state.refreshFlag != SmartSwipeStateFlag.REFRESHING) {
                state.animateOffsetTo(0f, 600)
            }
            return available
        }

        if (state.indicatorOffset < 0) {
            if (state.indicatorOffset < -state.footerHeight) {
                state.animateOffsetTo(-state.footerHeight)
            }
            return available
        }
        return super.onPostFling(consumed, available)
    }
}

@Composable
private fun SubComposeSmartSwipeRefresh(
    headerIndicator: (@Composable () -> Unit)?, footerIndicator: (@Composable () -> Unit)?, content: @Composable (header: Int, footer: Int) -> Unit
) {
    SubcomposeLayout { constraints ->
        val headerPlaceable = subcompose("header", headerIndicator ?: {}).firstOrNull()?.measure(constraints)
        val footerPlaceable = subcompose("footer", footerIndicator ?: {}).firstOrNull()?.measure(constraints)
        val contentPlaceable =
            subcompose("content") { content(headerPlaceable?.height ?: 0, footerPlaceable?.height ?: 0) }.first().measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.placeRelative(0, 0)
        }
    }
}
