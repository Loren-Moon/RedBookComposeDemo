package com.loren.redbookdemo.ui

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.google.gson.Gson
import com.loren.redbook.data.bean.ContentBean
import com.loren.redbook.home.navigation.HOME_DETAIL_ROUTE
import com.loren.redbook.home.navigation.navigateToHome
import com.loren.redbook.me.navigation.navigateToMe
import com.loren.redbook.message.navigation.navigateToMessage
import com.loren.redbook.shopping.navigation.navigateToShopping
import com.loren.redbookdemo.navigation.MainTopNavItem

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
): RedBookAppState {
    return remember(navController) {
        RedBookAppState(
            navController = navController,
            snackBarHostState = snackBarHostState
        )
    }
}

@Stable
class RedBookAppState(
    val navController: NavHostController,
    val snackBarHostState: SnackbarHostState
) {
    // 默认状态栏图标颜色是深色
    var iconIsLight by mutableStateOf(false)

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    fun navigateToContentDetail(contentBean: ContentBean) {
        navController.navigate(
            "$HOME_DETAIL_ROUTE/${
                Uri.encode(
                    Gson().toJson(contentBean)
                )
            }"
        )
    }
}

@Composable
fun rememberMainState(
    navController: NavHostController = rememberNavController(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
): MainAppState {
    return remember(navController) {
        MainAppState(
            navController = navController,
            snackBarHostState = snackBarHostState
        )
    }
}

@Stable
class MainAppState(
    val navController: NavHostController,
    val snackBarHostState: SnackbarHostState
) {
    val mainTopLevelDestinations: List<MainTopNavItem> = MainTopNavItem.entries

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    fun navigateToMainTopLevelDestination(topLevelDestination: MainTopNavItem, changeStatusBarIconMode: (Boolean) -> Unit) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (topLevelDestination) {
            MainTopNavItem.HOME -> {
                navController.navigateToHome(topLevelNavOptions)
                changeStatusBarIconMode(false)
            }

            MainTopNavItem.SHOPPING -> {
                navController.navigateToShopping(topLevelNavOptions)
                changeStatusBarIconMode(false)
            }

            MainTopNavItem.ADD -> {
                changeStatusBarIconMode(false)
            }

            MainTopNavItem.MESSAGE -> {
                navController.navigateToMessage(topLevelNavOptions)
                changeStatusBarIconMode(false)
            }

            MainTopNavItem.ME -> {
                navController.navigateToMe(topLevelNavOptions)
                changeStatusBarIconMode(true)
            }
        }
    }
}