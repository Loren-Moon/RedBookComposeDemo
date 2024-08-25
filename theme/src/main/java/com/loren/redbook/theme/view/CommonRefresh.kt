package com.loren.redbook.theme.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
                enableRefresh && available.y < 0 -> {
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
