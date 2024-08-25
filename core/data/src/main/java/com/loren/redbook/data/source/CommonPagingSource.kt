package com.loren.redbook.data.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.loren.redbook.data.bean.ContentBean
import com.loren.redbook.data.bean.UserBean
import kotlinx.coroutines.delay
import java.util.UUID
import kotlin.random.Random

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