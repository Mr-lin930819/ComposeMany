package com.mrlin.composemany.pages.mall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mrlin.composemany.pages.mall.category.MallHome
import com.mrlin.composemany.pages.music.widgets.MiniButton
import com.mrlin.composemany.ui.theme.ComposeManyTheme

/*********************************
 * 商城功能
 * @author mrlin
 * 创建于 2021年10月28日
 ******************************** */
class MallActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeManyTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") { PageContent(navController = navController) }
                    composable(MallScreen.Detail.route) {
                        Text(text = "详情")
                    }
                }
            }
        }
    }

}

/**
 * 内容页
 */
@Composable
private fun PageContent(navController: NavController) {
    val bottomMenus = listOf(MallScreen.Home, MallScreen.Category, MallScreen.ShopCart, MallScreen.Mine)
    //用于底部导航
    val bottomNavController = rememberNavController()
    Scaffold(bottomBar = {
        BottomNavigation {
            bottomMenus.forEach {
                MiniButton(iconRes = it.iconRes) {
                    bottomNavController.navigate(it.route) {
                        popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    }) {
        NavHost(navController = bottomNavController, startDestination = MallScreen.Home.route) {
            composable(MallScreen.Home.route) { MallHome(navController = navController) }
            composable(MallScreen.Category.route) {
                Text(text = "分类")
            }
            composable(MallScreen.ShopCart.route) {
                Text(text = "购物车")
            }
            composable(MallScreen.Mine.route) {
                Text(text = "我的")
            }
        }
    }
}