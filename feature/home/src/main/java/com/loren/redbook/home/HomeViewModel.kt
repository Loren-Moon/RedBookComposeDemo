package com.loren.redbook.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.loren.redbook.data.repo.CommonContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val commonContentRepository: CommonContentRepository
) : ViewModel() {

    val commonPagingFlow = commonContentRepository.commonPagingFlow.cachedIn(viewModelScope)
    val videoPagingFlow = commonContentRepository.videoPagingFlow.cachedIn(viewModelScope)
}
