package com.mrlin.composemany.pages.music.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mrlin.composemany.R
import com.mrlin.composemany.state.ViewState

/*********************************
 * 【我的】页面
 * @author mrlin
 * 创建于 2021年09月03日
 ******************************** */
@Composable
fun Mine(myPlayList: ViewState) {
    if (myPlayList !is MyPlayListLoaded) {
        //未载入状态
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(Modifier.fillMaxSize()) {
                Text(text = "我的", modifier = Modifier.align(Alignment.Center))
            }
        }
        return
    }
    val topMenus = listOf(
        "本地音乐" to R.drawable.icon_music,
        "最近播放" to R.drawable.icon_late_play,
        "下载管理" to R.drawable.icon_download_black,
        "我的电台" to R.drawable.icon_broadcast,
        "我的收藏" to R.drawable.icon_collect,
    )
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(topMenus.size) { index ->
            val menu = topMenus[index]
            Row(Modifier.height(56.dp), verticalAlignment = CenterVertically) {
                Image(
                    painter = painterResource(id = menu.second),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(10.dp)
                )
                Text(text = menu.first, modifier = Modifier.weight(5.0f))
            }
            Divider()
        }
        item { PlaylistTitle(title = "创建的歌单", count = myPlayList.selfCreates.size) }
        items(myPlayList.selfCreates) {
            Text(text = it.name)
        }
        item { PlaylistTitle(title = "收藏的歌单", count = myPlayList.collects.size) }
        items(myPlayList.collects) {
            Text(text = it.name)
        }
    }
}

/**
 * 歌单标题
 */
@Composable
private fun PlaylistTitle(title: String, count: Int) {
    Row {
        Image(painter = painterResource(id = R.drawable.icon_up), contentDescription = null)
        Text(text = "${title}  (${count})")
        Spacer(modifier = Modifier.weight(1.0f))
        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null, modifier = Modifier.size(20.dp))
    }
}