package com.loren.redbook.theme.view

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.fastFold
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * 官方的ScrollableTabRow有个默认的Tab最小宽度
 */
@Composable
fun RedBookTabRow(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    indicator: @Composable () -> Unit = {},
    tabs: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val scrollableTabData = remember(scrollState, coroutineScope) {
        ScrollableTabData(
            scrollState = scrollState,
            coroutineScope = coroutineScope
        )
    }
    Box(modifier = modifier.horizontalScroll(scrollState), contentAlignment = Alignment.Center) {
        SubcomposeLayout(modifier = Modifier.wrapContentSize()) { constraints ->
            val tabMeasurables = subcompose("tabs", tabs)
            val tabHeight = tabMeasurables.fastFold(initial = 0) { curr, measurable ->
                maxOf(curr, measurable.minIntrinsicHeight(Constraints.Infinity))
            }

            val tabPlaceables = mutableListOf<Placeable>()
            val tabPositions = mutableListOf<TabPosition>()
            var left = 0
            tabMeasurables.fastForEach {
                val placeable = it.measure(constraints.copy(minWidth = 0, maxWidth = Constraints.Infinity, minHeight = tabHeight, maxHeight = tabHeight))
                tabPlaceables.add(placeable)
                tabPositions.add(
                    TabPosition(left.toDp(), placeable.width.toDp(), placeable.width.toDp())
                )
                left += placeable.width
            }

            val layoutWidth = tabPlaceables.sumOf { it.width }

            val indicatorPlaceable =
                subcompose("indicator") {
                    Box(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]), contentAlignment = Alignment.Center) {
                        indicator()
                    }
                }.firstOrNull()?.measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
            val layoutHeight = max(tabHeight, indicatorPlaceable?.height ?: 0)
            layout(layoutWidth, layoutHeight) {
                indicatorPlaceable?.placeRelative(0, layoutHeight - indicatorPlaceable.height)

                left = 0
                tabPlaceables.forEach {
                    it.placeRelative(left, (layoutHeight - it.height) / 2)
                    left += it.width
                }

                scrollableTabData.onLaidOut(
                    density = this@SubcomposeLayout,
                    edgeOffset = 0,
                    tabPositions = tabPositions,
                    selectedTab = selectedIndex
                )
            }

        }
    }
}

fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "tabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = currentTabPosition.width,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    val indicatorOffset by animateDpAsState(
        targetValue = currentTabPosition.left,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}

private class ScrollableTabData(
    private val scrollState: ScrollState,
    private val coroutineScope: CoroutineScope
) {
    private var selectedTab: Int? = null

    fun onLaidOut(
        density: Density,
        edgeOffset: Int,
        tabPositions: List<TabPosition>,
        selectedTab: Int
    ) {
        // Animate if the new tab is different from the old tab, or this is called for the first
        // time (i.e selectedTab is `null`).
        if (this.selectedTab != selectedTab) {
            this.selectedTab = selectedTab
            tabPositions.getOrNull(selectedTab)?.let {
                // Scrolls to the tab with [tabPosition], trying to place it in the center of the
                // screen or as close to the center as possible.
                val calculatedOffset = it.calculateTabOffset(density, edgeOffset, tabPositions)
                if (scrollState.value != calculatedOffset) {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(
                            calculatedOffset,
                            animationSpec = tween(
                                durationMillis = 250,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                }
            }
        }
    }

    /**
     * @return the offset required to horizontally center the tab inside this TabRow.
     * If the tab is at the start / end, and there is not enough space to fully centre the tab, this
     * will just clamp to the min / max position given the max width.
     */
    private fun TabPosition.calculateTabOffset(
        density: Density,
        edgeOffset: Int,
        tabPositions: List<TabPosition>
    ): Int = with(density) {
        val totalTabRowWidth = tabPositions.last().right.roundToPx() + edgeOffset
        val visibleWidth = totalTabRowWidth - scrollState.maxValue
        val tabOffset = left.roundToPx()
        val scrollerCenter = visibleWidth / 2
        val tabWidth = width.roundToPx()
        val centeredTabOffset = tabOffset - (scrollerCenter - tabWidth / 2)
        // How much space we have to scroll. If the visible width is <= to the total width, then
        // we have no space to scroll as everything is always visible.
        val availableSpace = (totalTabRowWidth - visibleWidth).coerceAtLeast(0)
        return centeredTabOffset.coerceIn(0, availableSpace)
    }
}

@Immutable
class TabPosition internal constructor(val left: Dp, val width: Dp, val contentWidth: Dp) {

    val right: Dp get() = left + width

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TabPosition) return false

        if (left != other.left) return false
        if (width != other.width) return false
        if (contentWidth != other.contentWidth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + contentWidth.hashCode()
        return result
    }

    override fun toString(): String {
        return "TabPosition(left=$left, right=$right, width=$width, contentWidth=$contentWidth)"
    }
}