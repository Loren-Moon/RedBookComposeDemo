package com.loren.redbook.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.loren.redbook.data.bean.ContentBean

@Composable
fun HomeRoute(
    snackBarHostState: SnackbarHostState,
    onSettingClick: () -> Unit,
    navigateToDetail: (ContentBean) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HomeMain(snackBarHostState, onSettingClick, navigateToDetail)
    }
}

@Composable
fun HomeDetailRoute(
    contentBean: ContentBean?,
    navigateToHome: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        HomeDetail(contentBean, navigateToHome)
    }
}