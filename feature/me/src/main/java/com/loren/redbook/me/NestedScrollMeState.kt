package com.loren.redbook.me

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class NestedScrollMeState(private val coroutineScope: CoroutineScope, offsetSave: Float = 0f) {
    var topBarHeight = 0
    var contentBarHeight = 0
    var functionBarHeight = 0
    private val _offset = Animatable(offsetSave)
    private val mutatorMutex = MutatorMutex()

    val offset: Float
        get() = _offset.value

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

    private fun consumed(needConsumedY: Float) {
        if (needConsumedY == 0f) return
        coroutineScope.launch {
            snapOffsetTo(offset + needConsumedY)
        }
    }

    suspend fun animateOffsetTo(offset: Float, durationMillis: Int = AnimationConstants.DefaultDurationMillis) {
        mutatorMutex.mutate {
            _offset.animateTo(offset, animationSpec = tween(durationMillis))
        }
    }

    suspend fun snapOffsetTo(offset: Float) {
        mutatorMutex.mutate(MutatePriority.UserInput) {
            _offset.snapTo(offset)
        }
    }

    suspend fun scrollToFold() {
        animateOffsetTo(-(contentBarHeight - topBarHeight - functionBarHeight).toFloat())
    }

    /**
     * 消费顺序：父onPreScroll->子onPreScroll->子onPostScroll->父onPostScroll
     *
     * 问题记录：
     * 1、如果该connection放在最外层，HorizontalPager是子，connection会先消费onPostScroll，它的逻辑会CancellationException，详情查看[DefaultPagerNestedScrollConnection]
     * 2、如果该connection放在最内层，HorizontalPager是父
     */
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
}

@Composable
fun rememberNestedScrollMeState(coroutineScope: CoroutineScope) = rememberSaveable(saver = NestedScrollMeStateSaver(coroutineScope)) {
    NestedScrollMeState(coroutineScope)
}