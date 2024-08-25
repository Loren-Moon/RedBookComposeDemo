package com.loren.redbookdemo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import com.loren.redbook.data.bean.ContentBean
import com.loren.redbook.home.navigation.HOME_ROUTE
import com.loren.redbook.home.navigation.homeScreen
import com.loren.redbook.me.navigation.meScreen
import com.loren.redbook.message.navigation.messageScreen
import com.loren.redbook.shopping.navigation.shoppingScreen
import com.loren.redbookdemo.ui.MainAppState

@Composable
fun MainNavHost(
    appState: MainAppState,
    startDestination: String = HOME_ROUTE,
    onSettingClick: () -> Unit,
    navigateToContentDetail: (ContentBean) -> Unit
) {
    NavHost(
        navController = appState.navController,
        startDestination = startDestination
    ) {
        homeScreen(
            appState.snackBarHostState,
            onSettingClick,
            navigateToDetail = navigateToContentDetail
        )
        shoppingScreen()
        messageScreen()
        meScreen(appState.snackBarHostState, navigateToContentDetail = navigateToContentDetail)
    }
}