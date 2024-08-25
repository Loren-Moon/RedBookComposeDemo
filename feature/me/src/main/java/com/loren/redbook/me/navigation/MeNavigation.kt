package com.loren.redbook.me.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.loren.redbook.data.bean.ContentBean
import com.loren.redbook.me.MeRoute

const val ME_ROUTE = "me"

fun NavController.navigateToMe(navOptions: NavOptions) = navigate(ME_ROUTE, navOptions)

fun NavGraphBuilder.meScreen(
    snackBarHostState: SnackbarHostState,
    navigateToContentDetail: (ContentBean) -> Unit
) {
    composable(route = ME_ROUTE) {
        MeRoute(snackBarHostState, navigateToContentDetail)
    }
}