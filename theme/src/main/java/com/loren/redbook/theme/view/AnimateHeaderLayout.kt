package com.loren.redbook.theme.view

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity

@Composable
fun AnimateHeaderLayout(
    modifier: Modifier = Modifier,
    state: AnimateHeaderState = rememberAnimateHeaderState(),
    topBar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {

    LaunchedEffect(state.flag) {
        if (state.flag) {
            state.animateExpand()
        } else {
            state.animateFold()
        }
    }

    Box(modifier = modifier.clipToBounds()) {
        SubComposeAnimateHeaderLayout(
            topBar = topBar
        ) { topBarHeight ->
            state.topBarHeight = topBarHeight.toFloat()
            val top = with(LocalDensity.current) {
                (state.topBarHeight + state.topBarOffset).toDp()
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                //.nestedScroll(connection)
            ) {
                Box(modifier = Modifier.padding(top = top)) {
                    content()
                }
                Box(modifier = Modifier.graphicsLayer { translationY = state.topBarOffset }) {
                    topBar()
                }
            }
        }
    }
}

@Stable
class AnimateHeaderState {
    var topBarHeight = 0f
    var topBarOffset by mutableFloatStateOf(0f)
        private set

    // true-展开 false-折叠
    var flag by mutableStateOf(true)

    suspend fun animateExpand() {
        animate(initialValue = topBarOffset, targetValue = 0f, animationSpec = tween(200)) { value, velocity ->
            topBarOffset = value
        }
    }

    suspend fun animateFold() {
        animate(initialValue = topBarOffset, targetValue = -topBarHeight, animationSpec = tween(300)) { value, velocity ->
            topBarOffset = value
        }
    }
}

@Composable
fun rememberAnimateHeaderState(): AnimateHeaderState {
    return remember {
        AnimateHeaderState()
    }
}

@Composable
private fun SubComposeAnimateHeaderLayout(
    topBar: @Composable () -> Unit,
    content: @Composable (topBarHeight: Int) -> Unit
) {
    SubcomposeLayout { constraints ->
        val topBarPlaceable = subcompose("topBar", topBar).first().measure(constraints)
        val contentPlaceable = subcompose("content") { content(topBarPlaceable.height) }.first().measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.placeRelative(0, 0)
        }
    }
}