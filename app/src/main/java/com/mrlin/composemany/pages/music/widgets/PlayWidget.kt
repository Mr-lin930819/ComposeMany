package com.mrlin.composemany.pages.music.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.mrlin.composemany.R
import com.mrlin.composemany.pages.music.PlaySongsViewModel

/**
 * 页面下面的播放条
 */
@Composable
fun PlayWidget(viewModel: PlaySongsViewModel = viewModel(), onClick: () -> Unit) {
    val allSongs by viewModel.allSongs.collectAsState()
    val curIndex by viewModel.curIndex.collectAsState()
    val curSong = allSongs.getOrNull(curIndex)
    Box(
        modifier = Modifier
            .height(72.dp)
            .fillMaxWidth()
            .background(color = Color.White)
            .border(1.dp, color = Color.LightGray)
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        if (allSongs.isEmpty()) {
            Text(text = "暂无正在播放的歌曲", modifier = Modifier.align(Alignment.Center))
        } else {
            val curProgress by viewModel.curProgress.collectAsState()
            val isPlaying by viewModel.isPlaying.collectAsState()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberImagePainter(curSong?.picUrl.orEmpty()),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = curSong?.name.orEmpty(), maxLines = 1)
                    Text(text = curSong?.artists.orEmpty())
                }
                Spacer(modifier = Modifier.weight(1.0f))
                Box(modifier = Modifier.size(42.dp)) {
                    Icon(
                        painter = painterResource(if (isPlaying) R.drawable.icon_song_pause else R.drawable.icon_song_play),
                        contentDescription = null,
                        Modifier
                            .fillMaxSize()
                            .clickable {
                                viewModel.togglePlay()
                            }
                    )
                    CircularProgressIndicator(
                        progress = curProgress,
                        modifier = Modifier.padding(5.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}