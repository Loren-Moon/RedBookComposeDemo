package com.loren.redbook.me

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope

val ColorSaver = Saver<Color, Long>(
    save = { it.value.toLong() },
    restore = { Color(it.toULong()) }
)

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