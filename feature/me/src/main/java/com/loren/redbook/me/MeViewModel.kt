package com.loren.redbook.me

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.loren.redbook.data.bean.ContentBean
import com.loren.redbook.data.bean.UserBean
import com.loren.redbook.data.repo.CommonContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeViewModel @Inject constructor(
    private val commonContentRepository: CommonContentRepository
) : ViewModel() {

    val commonPagingFlow = commonContentRepository.commonPagingFlow.cachedIn(viewModelScope)
    private val _shareItemChannel = Channel<UserBean>()
    val shareItemFlow = _shareItemChannel.receiveAsFlow()

    fun shareItem(userBean: UserBean) {
        viewModelScope.launch {
            _shareItemChannel.send(userBean)
        }
    }
}

sealed interface MeUiState {
    data object Loading : MeUiState
    data class Success(val contentList: PagingData<ContentBean>) : MeUiState
}