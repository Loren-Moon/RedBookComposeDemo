@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.loren.redbookdemo.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.loren.redbook.data.LocalAnimatedVisibilityScope
import com.loren.redbook.data.LocalNavHostSharedTransitionScope
import com.loren.redbook.data.bean.ContentBean
import com.loren.redbook.data.composableWithCompositionLocal
import com.loren.redbook.data.ext.cancelRipperClick
import com.loren.redbook.home.navigation.homeDetailScreen
import com.loren.redbook.theme.RedBookTheme
import com.loren.redbookdemo.R
import com.loren.redbookdemo.navigation.MainNavHost
import com.loren.redbookdemo.navigation.MainTopNavItem
import com.loren.redbookdemo.ui.dialog.SettingsDialog
import com.loren.redbookdemo.ui.viewmodel.SettingsViewModel

const val MAIN_ROUTE = "main"

@Composable
fun RedBookApp(appState: RedBookAppState) {
    val settingsViewModel = hiltViewModel<SettingsViewModel>()

    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalNavHostSharedTransitionScope provides this
        ) {
            AppNavHost(appState) {
                settingsViewModel.setShowSettingsDialog(true)
            }
        }
    }

    if (settingsViewModel.shouldShowSettingsDialog) {
        SettingsDialog {
            settingsViewModel.setShowSettingsDialog(false)
        }
    }
}

@Composable
private fun AppNavHost(appState: RedBookAppState, showSettingDialog: () -> Unit) {
    NavHost(
        navController = appState.navController,
        startDestination = MAIN_ROUTE
    ) {
        composableWithCompositionLocal(MAIN_ROUTE) {
            MainRoute(
                mainAppState = rememberMainState(
                    rememberNavController(), appState.snackBarHostState
                ),
                settingClick = {
                    showSettingDialog()
                },
                navigateToContentDetail = {
                    appState.navigateToContentDetail(it)
                },
                changeStatusBarIconMode = {
                    appState.iconIsLight = it
                }
            )
        }

        homeDetailScreen {
            appState.navController.popBackStack()
        }
    }
}

@Composable
fun MainRoute(
    mainAppState: MainAppState,
    settingClick: () -> Unit,
    navigateToContentDetail: (ContentBean) -> Unit,
    changeStatusBarIconMode: (Boolean) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // 这里这样写原因是"我的"页面是沉浸式
        containerColor = RedBookTheme.colors.background,
        snackbarHost = {
            SnackbarHost(hostState = mainAppState.snackBarHostState)
        },
        topBar = {

        },
        bottomBar = {
            RedBookBottomBar(
                mainAppState.mainTopLevelDestinations,
                mainAppState::navigateToMainTopLevelDestination,
                mainAppState.currentDestination,
                changeStatusBarIconMode
            ) {
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            MainNavHost(appState = mainAppState, onSettingClick = settingClick, navigateToContentDetail = navigateToContentDetail)
        }
    }
}

@Composable
private fun RedBookBottomBar(
    topLevelDestinations: List<MainTopNavItem>,
    navigateToTopLevelDestination: (MainTopNavItem, (Boolean) -> Unit) -> Unit,
    currentDestination: NavDestination?,
    changeStatusBarIconMode: (Boolean) -> Unit,
    addEventClick: () -> Unit
) {
    val sharedTransitionScope = LocalNavHostSharedTransitionScope.current ?: return
    val animatedContentScope = LocalAnimatedVisibilityScope.current ?: return
    with(animatedContentScope) {
        with(sharedTransitionScope) {
            Row(
                modifier = Modifier
                    .renderInSharedTransitionScopeOverlay(
                        zIndexInOverlay = 1f,
                    )
                    .animateEnterExit(
                        enter = fadeIn(),
                        exit = fadeOut()
                    )
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(RedBookTheme.colors.background)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                verticalAlignment = Alignment.CenterVertically
            ) {
                topLevelDestinations.forEach {
                    val selected = currentDestination?.route == it.route
                    when (it) {
                        MainTopNavItem.ADD -> {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .cancelRipperClick {
                                        addEventClick()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(RedBookTheme.colors.theme, RoundedCornerShape(8.dp))
                                        .width(48.dp)
                                        .height(30.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        modifier = Modifier.size(16.dp),
                                        painter = painterResource(id = R.drawable.icon_add),
                                        contentDescription = "add",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        else -> {
                            RedBookBottomItemText(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                appNavItem = it,
                                isSelected = selected,
                                messageNum = if (MainTopNavItem.MESSAGE == it) 6 else if (MainTopNavItem.ME == it) 18 else null
                            ) {
                                navigateToTopLevelDestination(it, changeStatusBarIconMode)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Modifier.notificationDot(num: Int) =
    composed {
        val color = RedBookTheme.colors.theme
        val textMeasure = rememberTextMeasurer()
        drawWithContent {
            drawContent()
            val centerOffset = Offset(size.width - 3.dp.toPx(), 3.dp.toPx())
            drawCircle(
                color = color,
                radius = 6.dp.toPx(),
                center = centerOffset
            )
            val textLayoutResult = textMeasure.measure(
                AnnotatedString(if (num < 100) "$num" else "99+"),
                TextStyle(fontSize = 7.sp, color = Color.White)
            )
            drawText(
                textLayoutResult,
                topLeft = Offset(
                    centerOffset.x - textLayoutResult.size.width / 2f,
                    centerOffset.y - textLayoutResult.size.height / 2f
                )
            )
        }
    }

/**
 * 底部导航栏文本
 */
@Composable
private fun RedBookBottomItemText(
    modifier: Modifier = Modifier,
    appNavItem: MainTopNavItem,
    isSelected: Boolean,
    messageNum: Int? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .cancelRipperClick {
                onClick()
            }, contentAlignment = Alignment.Center
    ) {
        val fontSize by animateFloatAsState(
            targetValue = if (isSelected) 16.sp.value else 14.sp.value,
            animationSpec = tween(durationMillis = 100, easing = LinearEasing),
            label = "tabTextSize"
        )
        Text(
            modifier = if (messageNum != null) Modifier.notificationDot(messageNum) else Modifier,
            text = appNavItem.title,
            color = if (isSelected) RedBookTheme.colors.title else RedBookTheme.colors.body,
            lineHeight = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = fontSize.sp
        )
    }
}