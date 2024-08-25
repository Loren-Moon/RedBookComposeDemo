package com.loren.redbook.message.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.loren.redbook.message.MessageRoute

const val MESSAGE_ROUTE = "message"

fun NavController.navigateToMessage(navOptions: NavOptions) = navigate(MESSAGE_ROUTE, navOptions)

fun NavGraphBuilder.messageScreen() {
    composable(route = MESSAGE_ROUTE) {
        MessageRoute()
    }
}