package com.loren.redbook.home.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.loren.redbook.data.bean.ContentBean
import com.loren.redbook.data.composableWithCompositionLocal
import com.loren.redbook.home.HomeDetailRoute
import com.loren.redbook.home.HomeRoute

const val HOME_ROUTE = "home"
const val HOME_DETAIL_ROUTE = "home_detail"

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(HOME_ROUTE, navOptions)

fun NavGraphBuilder.homeScreen(
    snackBarHostState: SnackbarHostState,
    onSettingClick: () -> Unit,
    navigateToDetail: (ContentBean) -> Unit
) {
    composable(HOME_ROUTE) {
        HomeRoute(
            snackBarHostState,
            onSettingClick,
            navigateToDetail
        )
    }
}

fun NavGraphBuilder.homeDetailScreen(
    navigateToHome: () -> Unit
) {
    composableWithCompositionLocal(
        route = "$HOME_DETAIL_ROUTE/{content}",
        arguments = listOf(navArgument("content") { NavType.StringType })
    ) {
        val contentBean = try {
            Gson().fromJson(it.arguments?.getString("content") ?: "", ContentBean::class.java)
        } catch (e: Exception) {
            null
        }
        HomeDetailRoute(contentBean, navigateToHome)
    }
}
