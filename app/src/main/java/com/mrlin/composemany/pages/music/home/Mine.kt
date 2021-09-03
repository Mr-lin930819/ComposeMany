package com.mrlin.composemany.pages.music.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mrlin.composemany.R

/*********************************
 * 【我的】页面
 * @author mrlin
 * 创建于 2021年09月03日
 ******************************** */
@Composable
fun Mine() {
    val topMenus = mapOf(
        "本地音乐" to R.drawable.icon_music,
        "最近播放" to R.drawable.icon_late_play,
        "下载管理" to R.drawable.icon_download_black,
        "我的电台" to R.drawable.icon_broadcast,
        "我的收藏" to R.drawable.icon_collect,
    )
    Column(modifier = Modifier.fillMaxSize()) {
        topMenus.forEach {
            Row(Modifier.height(56.dp), verticalAlignment = CenterVertically) {
                Image(
                    painter = painterResource(id = it.value),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(10.dp)
                )
                Text(text = it.key, modifier = Modifier.weight(5.0f), textAlign = TextAlign.Center)
            }
            Divider()
        }
        PlaylistTitle(title = "创建的歌单", count = 0)
        PlaylistTitle(title = "收藏的歌单", count = 0)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
        ) {
            Box(Modifier.fillMaxSize()) {
                Text(text = "我的", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

/**
 * 歌单标题
 */
@Composable
private fun PlaylistTitle(title: String, count: Long) {
    Row {
        Image(painter = painterResource(id = R.drawable.icon_up), contentDescription = null)
        Text(text = "${title}  (${count})")
        Spacer(modifier = Modifier.weight(1.0f))
        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null, modifier = Modifier.size(20.dp))
    }
}