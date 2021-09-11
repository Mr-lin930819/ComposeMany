package com.mrlin.composemany.pages.music.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.mrlin.composemany.pages.music.widgets.CircleAvatar
import com.mrlin.composemany.repository.entity.User
import com.mrlin.composemany.ui.theme.ComposeManyTheme
import com.mrlin.composemany.ui.theme.LightGray
import kotlinx.coroutines.launch

/*********************************
 * 音乐主页
 * @author mrlin
 * 创建于 2021年08月20日
 ******************************** */
@Composable
fun MusicHome(
    user: User?,
    modifier: Modifier = Modifier,
    musicHomeViewModel: MusicHomeViewModel = hiltViewModel(),
    onToScreen: ((Any) -> Unit)? = null
) {
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        drawerContent = { Drawer(user = user) },
        scaffoldState = scaffoldState,
        modifier = modifier
    ) {
        Home(musicHomeViewModel, onDrawerClick = {
            coroutineScope.launch {
                scaffoldState.drawerState.open()
            }
        }, onToScreen = onToScreen)
    }
}

@Composable
private fun Drawer(user: User?) {
    Column(
        Modifier
            .background(LightGray)
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircleAvatar(url = user?.profile?.avatarUrl)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "${user?.profile?.nickname.orEmpty()} >")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
        ) {
            DrawerItem(title = "设置", Icons.Outlined.Settings)
            Divider()
            DrawerItem(title = "夜间模式", Icons.Outlined.Check)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
        ) {
            DrawerItem(title = "我的订单", Icons.Outlined.MoreVert)
            Divider()
            DrawerItem(title = "关于", Icons.Outlined.Info)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(text = "退出登录/关闭", color = Color.Red)
        }
    }
}

@Composable
private fun DrawerItem(title: String, imageVector: ImageVector? = null) {
    Row(modifier = Modifier.padding(16.dp)) {
        imageVector?.let {
            Icon(imageVector = it, contentDescription = null)
        }
        Text(text = title, modifier = Modifier.padding(start = 16.dp))
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}

@Composable
@OptIn(ExperimentalPagerApi::class)
private fun Home(
    vm: MusicHomeViewModel? = null,
    onDrawerClick: (() -> Unit)? = null,
    onToScreen: ((Any) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = LightGray)
    ) {
        val discoveryViewData by vm?.discoveryData?.collectAsState() ?: return
        val myPlayList by vm?.myPlayList?.collectAsState() ?: return
        Row(
            Modifier
                .height(56.dp)
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { onDrawerClick?.invoke() }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Menu, contentDescription = null, tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.LightGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "大家都在搜 陈奕迅", color = Color.LightGray)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
        val pages: List<Pair<String, @Composable () -> Unit>> = listOf(
            "发现" to { Discovery(discoveryViewData, onToScreen = onToScreen) },
            "我的" to { Mine(myPlayList, onToScreen = onToScreen) },
            "动态" to { NewAction() }
        )
        val pagerState = rememberPagerState(pageCount = 3)
        val pagerScope = rememberCoroutineScope()
        TabRow(selectedTabIndex = pagerState.currentPage, backgroundColor = Color.Transparent) {
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

@Composable
private fun Tab(tab: String, selected: Boolean, onClick: () -> Unit) {
    Tab(
        selected = selected, onClick = onClick, modifier = Modifier.height(48.dp)
    ) {
        Text(text = tab)
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
        Home()
    }
}