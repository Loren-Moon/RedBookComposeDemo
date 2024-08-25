package com.loren.redbook.shopping.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.loren.redbook.shopping.ShoppingRoute

const val SHOPPING_ROUTE = "shopping"

fun NavController.navigateToShopping(navOptions: NavOptions) = navigate(SHOPPING_ROUTE, navOptions)

fun NavGraphBuilder.shoppingScreen() {
    composable(route = SHOPPING_ROUTE) {
        ShoppingRoute()
    }
}