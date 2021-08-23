package com.mrlin.composemany.pages.music.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.mrlin.composemany.pages.music.MusicScreen
import com.mrlin.composemany.pages.music.NetEaseMusicViewModel
import com.mrlin.composemany.repository.entity.Account
import com.mrlin.composemany.repository.entity.Profile
import com.mrlin.composemany.repository.entity.User
import com.mrlin.composemany.ui.theme.ComposeManyTheme
import kotlinx.coroutines.launch
import java.util.*

/*********************************
 * 音乐主页
 * @author mrlin
 * 创建于 2021年08月20日
 ******************************** */
@Composable
fun MusicHomePage(
    user: User?, musicViewModel: NetEaseMusicViewModel = viewModel(), onToScreen: ((MusicScreen) -> Unit)? = null
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeScreen.Home.route) {
        composable(HomeScreen.Home.route) {
            Home(user, musicViewModel, onToScreen = {
                if (it is HomeScreen) {
                    navController.navigate(it.route)
                } else if (it is MusicScreen) {
                    // compose的Navigation【当前2.4.0-alpha06】暂不支持复杂类型
                    // 带复杂参数的页面跳转需要改用Activity或Fragment级别的Navigator
                    onToScreen?.invoke(it)
                }
            })
        }
        composable(HomeScreen.DailySong.route) {
            Text(text = "每日推荐")
        }
        composable(HomeScreen.TopList.route) {
            Text(text = "排行榜")
        }
    }
}

@Composable
@OptIn(ExperimentalPagerApi::class)
private fun Home(
    user: User?,
    vm: NetEaseMusicViewModel? = null,
    onToScreen: ((Any) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        val discoveryViewData by vm?.discoveryData?.collectAsState() ?: return
        LaunchedEffect(key1 = true, block = {
            vm?.loadDiscoveryPage()
        })
        Row(
            Modifier
                .fillMaxHeight(0.12f)
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.primary)
                .padding(10.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = rememberImagePainter(user?.profile?.avatarUrl.orEmpty(),
                    builder = {
                        transformations(CircleCropTransformation())
                    }), contentDescription = null
            )
            Column {
                Text(text = user?.profile?.nickname.orEmpty(), style = MaterialTheme.typography.h5)
            }
            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.size(30.dp, 30.dp)) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
            }
        }
        val pages: List<Pair<String, @Composable () -> Unit>> = listOf(
            "发现" to { Discovery(discoveryViewData, onToScreen = onToScreen) },
            "我的" to { My() },
            "动态" to { NewAction() }
        )
        val pagerState = rememberPagerState(pageCount = 3)
        val pagerScope = rememberCoroutineScope()
        TabRow(selectedTabIndex = pagerState.currentPage, backgroundColor = Color.White) {
            pages.forEachIndexed { index, page ->
                Tab(tab = page.first, selected = index == pagerState.currentPage) {
                    pagerScope.launch { pagerState.scrollToPage(index) }
                }
            }
        }
        HorizontalPager(
            state = pagerState, verticalAlignment = Alignment.Top
        ) { page -> pages[page].second() }
    }
}

internal sealed class HomeScreen(open val route: String) {
    object Home : HomeScreen("home")
    object DailySong : HomeScreen("dailySong")
    object TopList : HomeScreen("topList")
}

@Composable
private fun Tab(tab: String, selected: Boolean, onClick: () -> Unit) {
    Tab(
        selected = selected, onClick = onClick, modifier = Modifier.height(48.dp)
    ) {
        Text(text = tab)
    }
}

@Composable
private fun My() {
    Box(Modifier.fillMaxSize()) {
        Text(text = "我的", modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun NewAction() {
    Box(Modifier.fillMaxSize()) {
        Text(text = "动态", modifier = Modifier.align(Alignment.Center))
    }
}

@Preview
@Composable
fun MusicHomePagePreview() {
    ComposeManyTheme {
        Home(
            user = User(
                0, 200, Account(
                    id = 0,
                    userName = "测试",
                    type = 0,
                    status = 200,
                    whitelistAuthority = 0,
                    createTime = Date().time.toInt(),
                    salt = "",
                    tokenVersion = 0,
                    ban = 0,
                    baoyueVersion = 0,
                    donateVersion = 0,
                    vipType = 0,
                    viptypeVersion = 0,
                    anonimousUser = false
                ), Profile(
                    "",
                    "你好",
                    "",
                    signature = "我的app"
                )
            )
        )
    }
}