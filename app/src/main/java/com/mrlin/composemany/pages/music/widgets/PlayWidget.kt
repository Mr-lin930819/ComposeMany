package com.mrlin.composemany.pages.music.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.mrlin.composemany.pages.music.PlaySongsViewModel

/**
 * 页面下面的播放条
 */
@Composable
fun PlayWidget(viewModel: PlaySongsViewModel = viewModel()) {
    val allSongs by viewModel.allSongs.collectAsState()
    val curIndex by viewModel.curIndex.collectAsState()
    val curSong = allSongs.getOrNull(curIndex)
    Box(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
            .border(1.dp, color = Color.LightGray)
            .padding(8.dp)
    ) {
        if (allSongs.isEmpty()) {
            Text(text = "暂无正在播放的歌曲", modifier = Modifier.align(Alignment.Center))
        } else {
            val curProgress by viewModel.curProgress.collectAsState()
            val isPlaying by viewModel.isPlaying.collectAsState()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberImagePainter(curSong?.picUrl?.value.orEmpty()),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = curSong?.name.orEmpty(), maxLines = 1)
                    Text(text = curSong?.artists.orEmpty())
                }
                Slider(value = curProgress, onValueChange = {
                    viewModel.trySeek(it)
                }, onValueChangeFinished = {
                    viewModel.seekPlay()
                }, modifier = Modifier.weight(1.0f))
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Menu else Icons.Default.PlayArrow,
                    contentDescription = null,
                    Modifier
                        .size(36.dp)
                        .clickable {
                            viewModel.togglePlay()
                        }
                )
            }
        }
    }
}