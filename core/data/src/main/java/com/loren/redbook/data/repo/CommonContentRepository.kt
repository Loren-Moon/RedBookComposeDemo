package com.loren.redbook.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.loren.redbook.data.bean.UserBean
import com.loren.redbook.data.di.Dispatcher
import com.loren.redbook.data.di.MockDataModule
import com.loren.redbook.data.di.MyDispatchers
import com.loren.redbook.data.source.CommonPagingSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CommonContentRepository @Inject constructor(
    @Dispatcher(MyDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    @MockDataModule.MockContentPic private val pictures: List<Int>,
    private val mockUsers: List<UserBean>
) {

    val commonPagingFlow = Pager(
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            prefetchDistance = 4
        ),
        pagingSourceFactory = { CommonPagingSource(pictures, mockUsers, isVideo = false, mockPages = 3) }
    ).flow.flowOn(ioDispatcher)

    val videoPagingFlow = Pager(
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            prefetchDistance = 4
        ),
        pagingSourceFactory = { CommonPagingSource(pictures, mockUsers, isVideo = true, mockPages = 5) }
    ).flow.flowOn(ioDispatcher)

}