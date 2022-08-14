package com.mrlin.composemany.pages.mall.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mrlin.composemany.pages.mall.MallScreen
import com.mrlin.composemany.ui.theme.LightGray
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.MapMarked
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

/*********************************
 * 首页
 * @author mrlin
 * 创建于 2021年10月28日
 ******************************** */
fun NavGraphBuilder.mallHome(navController: NavController) {
    navigation(startDestination = MallScreen.Home.Main.route, route = MallScreen.Home.route) {
        composable(MallScreen.Home.Main.route) {
            Text(text = "主页")
        }
    }
}

@Composable
fun MallHome(navController: NavController) {
    val state = rememberCollapsingToolbarScaffoldState()
    CollapsingToolbarScaffold(
        modifier = Modifier.fillMaxSize(),
        state = state,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbar = {
            val toolbarState = state.toolbarState
            Box(
                modifier = Modifier
                    .height(96.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary)
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.MapMarked, contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp), tint = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .road(Alignment.TopStart, Alignment.BottomCenter)
                    .height(48.dp)
                    .fillMaxWidth(0.6f + toolbarState.progress * 0.3f)
                    .padding(top = 8.dp, end = 8.dp, bottom = 8.dp, start = (8 + (1 - toolbarState.progress) * 32f).dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(LightGray),
            ) {
                Text(text = "推广内容", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            }
        }
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(60) {
                Text(text = it.toString())
            }
        }
    }
}