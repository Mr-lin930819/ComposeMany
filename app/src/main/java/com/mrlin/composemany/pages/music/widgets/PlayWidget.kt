package com.mrlin.composemany.pages.music.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            .padding(8.dp)
    ) {
        if (allSongs.isEmpty()) {
            Text(text = "暂无正在播放的歌曲", modifier = Modifier.align(Alignment.Center))
        } else {
            Row {
                Image(
                    painter = rememberImagePainter(curSong?.picUrl?.value.orEmpty()),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = curSong?.name.orEmpty(), maxLines = 1)
                    Text(text = curSong?.artists.orEmpty())
                }
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    Modifier.size(36.dp)
                )
            }
        }
    }
}