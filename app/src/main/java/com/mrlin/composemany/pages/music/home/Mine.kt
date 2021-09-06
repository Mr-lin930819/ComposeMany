package com.mrlin.composemany.pages.music.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.material.placeholder
import com.mrlin.composemany.R
import com.mrlin.composemany.pages.music.MusicScreen
import com.mrlin.composemany.repository.entity.*
import com.mrlin.composemany.state.ViewState

/*********************************
 * 【我的】页面
 * @author mrlin
 * 创建于 2021年09月03日
 ******************************** */
@Composable
fun Mine(myPlayList: ViewState, onToScreen: ((Any) -> Unit)? = null) {
    val (loaded, playList) = if (myPlayList !is MyPlayListLoaded) {
        //未载入状态
        false to MyPlayListLoaded(emptyList(), null)
    } else {
        true to myPlayList
    }
    val topMenus = listOf(
        "本地音乐" to R.drawable.icon_music,
        "最近播放" to R.drawable.icon_late_play,
        "下载管理" to R.drawable.icon_download_black,
        "我的电台" to R.drawable.icon_broadcast,
        "我的收藏" to R.drawable.icon_collect,
    )
    var selfCreatesExpand by remember { mutableStateOf(true) }
    var collectsExpand by remember { mutableStateOf(false) }
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
                        .placeholder(!loaded)
                )
                Text(text = menu.first, modifier = Modifier.weight(5.0f))
            }
            Divider()
        }
        item {
            PlaylistTitle(title = "创建的歌单", count = playList.selfCreates.size, selfCreatesExpand) {
                selfCreatesExpand = !selfCreatesExpand
            }
        }
        if (selfCreatesExpand) {
            items(playList.selfCreates) {
                PlaylistItem(playList = it) { onToScreen?.toPlayListScreen(it) }
            }
        }
        item {
            PlaylistTitle(title = "收藏的歌单", count = playList.collects.size, collectsExpand) {
                collectsExpand = !collectsExpand
            }
        }
        if (collectsExpand) {
            items(playList.collects) {
                PlaylistItem(playList = it) { onToScreen?.toPlayListScreen(it) }
            }
        }
    }
}

/**
 * 歌单标题
 */
@Composable
private fun PlaylistTitle(title: String, count: Int, expanded: Boolean, onToggleExpand: () -> Unit) {
    Row(
        verticalAlignment = CenterVertically, modifier = Modifier
            .height(48.dp)
            .clickable { onToggleExpand() }
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = if (expanded) R.drawable.icon_up else R.drawable.icon_down),
            contentDescription = null,
            Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$title  (${count})")
        Spacer(modifier = Modifier.weight(1.0f))
        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null, modifier = Modifier.size(20.dp))
    }
}

/**
 * 歌单项
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
private fun PlaylistItem(playList: PlayList, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(72.dp)
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = CenterVertically
    ) {
        Image(painter = rememberImagePainter(data = playList.coverImgUrl.limitSize(80)), contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = playList.name)
            Text(text = "${playList.trackCount}首", style = MaterialTheme.typography.subtitle2)
        }
    }
}

/**
 * 跳转到歌单详情页
 */
private fun ((Any) -> Unit).toPlayListScreen(playList: PlayList) {
    this(
        MusicScreen.PlayList(
            Recommend(
                id = playList.id,
                name = playList.name,
                playcount = playList.playCount,
                picUrl = playList.coverImgUrl
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
fun MinePreview() {
    Mine(
        myPlayList = MyPlayListLoaded(
            listOf(
                PlayList(
                    listOf(
                        Track("第一首", 1, 21, listOf(Ar(1, "歌手1")), Al(1, "Al1")),
                        Track("第二首", 2, 22, listOf(Ar(2, "歌手2")), Al(2, "Al2")),
                        Track("第三首", 3, 23, listOf(Ar(3, "歌手3")), Al(3, "Al3"))
                    )
                )
            ), User(
                0, 0, Account(1, "用户", 0, 0, 0, 0, "", 0, 0, 0, 0, 0, 0, false),
                Profile("", "", "")
            )
        )
    )
}