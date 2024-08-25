package com.loren.redbookdemo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.loren.redbook.theme.AppThemeType
import com.loren.redbook.theme.RedBookTheme
import com.loren.redbookdemo.ui.MAIN_ROUTE
import com.loren.redbookdemo.ui.RedBookApp
import com.loren.redbookdemo.ui.rememberAppState
import com.loren.redbookdemo.ui.viewmodel.MainActivityUiState
import com.loren.redbookdemo.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        onBackPressedDispatcher.addCallback {
            finish()
        }

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.uiState
                    .onEach {
                        uiState = it
                    }
                    .collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                MainActivityUiState.Loading -> true
                is MainActivityUiState.Success -> false
            }
        }

        setContent {
            val appState = rememberAppState()
            val lorenTheme = theme(uiState)
            // statusBar图标颜色模式
            val isDark = if (appState.iconIsLight && appState.currentDestination?.route == MAIN_ROUTE) true else AppThemeType.isDark(
                themeType = lorenTheme
            )

            DisposableEffect(lorenTheme, isDark) {
                enableEdgeToEdge(
                    SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { isDark },
                    SystemBarStyle.auto(Color.WHITE, Color.BLACK) { isDark },
                )
                onDispose { }
            }

            RedBookTheme(themeType = lorenTheme) {
                RedBookApp(appState)
            }
        }
    }

    @Composable
    private fun theme(
        uiState: MainActivityUiState,
    ): AppThemeType = when (uiState) {
        MainActivityUiState.Loading -> {
            AppThemeType.FOLLOW_SYSTEM
        }

        is MainActivityUiState.Success -> uiState.userData.theme
    }
}

