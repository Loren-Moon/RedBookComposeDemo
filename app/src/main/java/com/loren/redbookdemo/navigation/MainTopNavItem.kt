package com.loren.redbookdemo.navigation

import com.loren.redbook.home.navigation.HOME_ROUTE
import com.loren.redbook.me.navigation.ME_ROUTE
import com.loren.redbook.message.navigation.MESSAGE_ROUTE
import com.loren.redbook.shopping.navigation.SHOPPING_ROUTE

enum class MainTopNavItem(val title: String, val route: String) {
    HOME(title = "首页", route = HOME_ROUTE), SHOPPING("购物", SHOPPING_ROUTE), ADD("发布", ""), MESSAGE("消息", MESSAGE_ROUTE), ME("我", ME_ROUTE);
}