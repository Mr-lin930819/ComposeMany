package com.mrlin.composemany.pages.music.playsong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.BlurTransformation
import coil.transform.CircleCropTransformation
import com.mrlin.composemany.R
import com.mrlin.composemany.pages.music.PlaySongsViewModel
import com.mrlin.composemany.pages.music.home.composeContent
import com.mrlin.composemany.repository.entity.Song
import com.mrlin.composemany.repository.entity.limitSize
import com.mrlin.composemany.ui.theme.ComposeManyTheme
import dagger.hilt.android.AndroidEntryPoint

/*********************************
 * 歌曲播放
 * @author mrlin
 * 创建于 2021年09月06日
 ******************************** */
@AndroidEntryPoint
class PlaySongFragment : Fragment() {
    private val playSongViewModel by activityViewModels<PlaySongsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return composeContent {
            val curSongIndex by playSongViewModel.curIndex.collectAsState()
            val curSong = playSongViewModel.allSongs.value.getOrNull(curSongIndex)
            val curProgress by playSongViewModel.curProgress.collectAsState()
            val isPlaying by playSongViewModel.isPlaying.collectAsState()
            ComposeManyTheme {
                PlaySong(song = curSong, curProgress, isPlaying) {
                    when (it) {
                        is Event.TrySeek -> playSongViewModel.trySeek(it.progress)
                        is Event.Seek -> playSongViewModel.seekPlay()
                        is Event.TogglePlay -> playSongViewModel.togglePlay()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun PlaySong(
    song: Song?,
    progress: Float = 0f,
    isPlaying: Boolean = false,
    onEvent: ((Event) -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column {
                    Text(text = song?.name.orEmpty())
                    Text(text = song?.artists.orEmpty(), style = MaterialTheme.typography.caption)
                }
            })
        }
    ) {
        Image(painter = rememberImagePainter(song?.picUrl?.limitSize(200), builder = {
            transformations(BlurTransformation(LocalContext.current, 20f))
        }), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillHeight)
        Column(modifier = Modifier.padding(bottom = 12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(36.dp)
            ) {
                Image(
                    painter = rememberImagePainter(song?.picUrl?.limitSize(200), builder = {
                        transformations(CircleCropTransformation())
                    }), contentDescription = null, modifier = Modifier
                        .align(Alignment.Center)
                        .height(160.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.bet),
                    contentDescription = null,
                    modifier = Modifier
                        .matchParentSize()
                        .align(
                            Alignment.Center
                        )
                )
            }
            //歌词显示
            Spacer(modifier = Modifier.weight(1.0f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp), horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id = R.drawable.icon_song_download), contentDescription = null)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id = R.drawable.bfc), contentDescription = null)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id = R.drawable.icon_song_comment), contentDescription = null)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id = R.drawable.icon_song_more), contentDescription = null)
                }
            }
            Slider(value = progress, onValueChange = {
                onEvent?.invoke(Event.TrySeek(it))
            }, onValueChangeFinished = {
                onEvent?.invoke(Event.Seek)
            }, modifier = Modifier
                .height(64.dp)
                .padding(10.dp)
            )
            Row(
                modifier = Modifier
                    .height(96.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id = R.drawable.icon_song_play_type_1), contentDescription = null)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id = R.drawable.icon_song_left), contentDescription = null)
                }
                IconButton(onClick = { onEvent?.invoke(Event.TogglePlay) }) {
                    Icon(
                        painter = painterResource(id = if (isPlaying) R.drawable.icon_song_pause else R.drawable.icon_song_play),
                        contentDescription = null
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id = R.drawable.icon_song_right), contentDescription = null)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id = R.drawable.icon_play_songs), contentDescription = null)
                }
            }
        }
    }
}

private sealed class Event {
    class TrySeek(val progress: Float) : Event()

    object Seek : Event()

    object TogglePlay : Event()
}

@Preview
@Composable
fun PlaySongPreview() {
    PlaySong(song = null)
}
