package com.loren.redbook.theme.view

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.loren.redbook.theme.RedBookTheme

@Composable
fun <T : Any> LazyVerticalStaggeredGridWithPaging(
    lazyPagingItems: LazyPagingItems<T>,
    columns: StaggeredGridCells,
    modifier: Modifier = Modifier,
    state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalItemSpacing: Dp = 0.dp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(0.dp),
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    item: @Composable (T, Int) -> Unit
) {
    if (lazyPagingItems.loadState.refresh is LoadState.Loading && lazyPagingItems.itemCount == 0) {
        CircleLoading(
            modifier = Modifier.fillMaxSize()
        )
    } else {
        LazyVerticalStaggeredGrid(
            columns = columns,
            modifier = modifier,
            state = state,
            contentPadding = contentPadding,
            reverseLayout = reverseLayout,
            verticalItemSpacing = verticalItemSpacing,
            horizontalArrangement = horizontalArrangement,
            flingBehavior = flingBehavior,
            userScrollEnabled = userScrollEnabled
        ) {
            items(lazyPagingItems.itemCount) {
                val bean = lazyPagingItems[it]
                if (bean != null) {
                    item(bean, it)
                }
            }
            pagingLoadMore(lazyPagingItems)
        }
    }
}

private fun LazyStaggeredGridScope.pagingLoadMore(pagingItems: LazyPagingItems<out Any>) {
    when {
//        pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount == 0 -> {
//            // 第一次加载
//            item(span = StaggeredGridItemSpan.FullLine) {
//                CircleLoading(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(300.dp)
//                )
//            }
//        }

        pagingItems.loadState.append is LoadState.Loading -> {
            // 加载更多底部loading
            item(span = StaggeredGridItemSpan.FullLine) {
                CircleLoading()
            }
        }

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

    }
}