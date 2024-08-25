package com.loren.redbookdemo.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loren.redbook.theme.AppThemeType
import com.loren.redbookdemo.repo.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    val settingsUiState: StateFlow<SettingsUiState>
        get() = userDataRepository.userData.map {
            SettingsUiState.Success(it)
        }.stateIn(
            scope = viewModelScope,
            initialValue = SettingsUiState.Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    var shouldShowSettingsDialog by mutableStateOf(false)
        private set

    fun setShowSettingsDialog(shouldShow: Boolean) {
        shouldShowSettingsDialog = shouldShow
    }

    fun updateThemeConfig(appThemeType: AppThemeType) {
        viewModelScope.launch {
            userDataRepository.updateThemeConfig(appThemeType)
        }
    }

}

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val userData: UserData) : SettingsUiState
}