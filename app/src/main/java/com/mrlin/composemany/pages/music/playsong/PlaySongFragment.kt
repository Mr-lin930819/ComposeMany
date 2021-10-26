package com.mrlin.composemany.pages.music.playsong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.BlurTransformation
import coil.transform.CircleCropTransformation
import com.mrlin.composemany.R
import com.mrlin.composemany.pages.music.MusicScreen
import com.mrlin.composemany.pages.music.PlaySongsViewModel
import com.mrlin.composemany.pages.music.home.composeContent
import com.mrlin.composemany.pages.music.widgets.MiniButton
import com.mrlin.composemany.repository.entity.Song
import com.mrlin.composemany.repository.entity.SongCommentData
import com.mrlin.composemany.repository.entity.limitSize
import com.mrlin.composemany.ui.theme.ComposeManyTheme
import com.mrlin.composemany.utils.simpleNumText
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

/*********************************
 * 歌曲播放
 * @author mrlin
 * 创建于 2021年09月06日
 ******************************** */
@AndroidEntryPoint
class PlaySongFragment : Fragment() {
    private val playSongViewModel by activityViewModels<PlaySongsViewModel>()
    private val viewModel by viewModels<SongViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composeContent {
        val curSong by playSongViewModel.curSong.collectAsState()
        val curProgress by playSongViewModel.curProgress.collectAsState()
        val isPlaying by playSongViewModel.isPlaying.collectAsState()
        val commentData by viewModel.songComment.collectAsState()
        val likeList by playSongViewModel.likeList.collectAsState()
        val lyric by playSongViewModel.lyric.collectAsState()
        LaunchedEffect(key1 = curSong, block = {
            //歌曲更换后自动载入对应评论
            viewModel.loadComment(curSong ?: return@LaunchedEffect)
        })
        ComposeManyTheme {
            val likeSong = likeList.contains(curSong?.id)
            PlaySong(song = curSong, curProgress, isPlaying, commentData, likeSong, lyric) {
                when (it) {
                    is Event.TrySeek -> playSongViewModel.trySeek(it.progress)
                    is Event.Seek -> playSongViewModel.seekPlay()
                    is Event.TogglePlay -> playSongViewModel.togglePlay()
                    is Event.Back -> findNavController().navigateUp()
                    is Event.ToComments -> findNavController().navigate(
                        MusicScreen.SongComment(curSong ?: return@PlaySong).directions
                    )
                    is Event.PreviousSong -> playSongViewModel.prevPlay()
                    is Event.NextSong -> playSongViewModel.nextPlay()
                    is Event.LikeSong -> playSongViewModel.toggleLike()
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
    commentData: SongCommentData? = null,
    likeSong: Boolean = false,
    lyric: List<Pair<Int, String>> = emptyList(),
    onEvent: ((Event) -> Unit)? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column {
                    Text(text = song?.name.orEmpty())
                    Text(
                        text = song?.artists.orEmpty(),
                        style = TextStyle(fontSize = 12.sp, color = Color.White)
                    )
                }
            }, navigationIcon = {
                IconButton(onClick = { onEvent?.invoke(Event.Back) }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            })
        }
    ) {
        //模糊虚化的封面作为背景
        Image(
            painter = rememberImagePainter(song?.picUrl?.limitSize(200), builder = {
                transformations(BlurTransformation(LocalContext.current, 16f))
            }),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    //背景遮上半透明颜色，改善明亮色调的背景下，白色操作按钮的显示效果
                    drawRect(Color.Gray, alpha = 0.7f)
                },
            contentScale = ContentScale.FillHeight
        )
        Column(modifier = Modifier.padding(bottom = 12.dp)) {
            var showLyric by remember { mutableStateOf(false) }
            val cdAreaModifier = Modifier
                .fillMaxWidth()
                .weight(1.0f)
                .clickable { showLyric = !showLyric }
            if (showLyric) {
                //歌词显示
                Lyric(cdAreaModifier, lyric, progress)
            } else {
                //唱片显示
                CD(isPlaying, song, modifier = cdAreaModifier)
            }
            //评论、收藏等歌曲操作
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(64.dp), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniButton(
                    if (likeSong) R.drawable.icon_like else R.drawable.icon_dislike,
                    tint = if (likeSong) Color.Red else Color.LightGray
                ) {
                    onEvent?.invoke(Event.LikeSong)
                }
                MiniButton(R.drawable.icon_song_download)
                MiniButton(R.drawable.bfc)
                Box(modifier = Modifier.fillMaxHeight()) {
                    MiniButton(R.drawable.icon_song_comment) { onEvent?.invoke(Event.ToComments) }
                    Text(
                        text = commentData?.total?.toLong()?.simpleNumText().orEmpty(),
                        modifier = Modifier.align(BiasAlignment(0.75f, -0.75f)),
                        style = TextStyle(color = Color.White, fontSize = 10.sp)
                    )
                }
                MiniButton(R.drawable.icon_song_more)
            }
            //歌曲进度
            Slider(value = progress, onValueChange = {
                onEvent?.invoke(Event.TrySeek(it))
            }, onValueChangeFinished = {
                onEvent?.invoke(Event.Seek)
            }, modifier = Modifier
                .height(48.dp)
                .padding(10.dp)
            )
            //播放操作
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MiniButton(R.drawable.icon_song_play_type_1)
                MiniButton(R.drawable.icon_song_left)
                MiniButton(if (isPlaying) R.drawable.icon_song_pause else R.drawable.icon_song_play) {
                    onEvent?.invoke(Event.TogglePlay)
                }
                MiniButton(R.drawable.icon_song_right) {
                    onEvent?.invoke(Event.NextSong)
                }
                MiniButton(R.drawable.icon_play_songs)
            }
        }
    }
}

@Composable
private fun CD(isPlaying: Boolean, song: Song?, modifier: Modifier) {
    Box(
        modifier = modifier.padding(horizontal = 36.dp)
    ) {
        //唱片旋转角度
        val rotation = infiniteRotation(isPlaying)
        //唱针旋转角度
        val stylusRotation by animateFloatAsState(targetValue = if (isPlaying) 0f else -30f)
        //歌曲封面
        Image(
            painter = rememberImagePainter(song?.picUrl?.limitSize(200), builder = {
                transformations(CircleCropTransformation())
            }),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .matchParentSize()
                .aspectRatio(1.0f)
                .padding(20.dp)
                .graphicsLayer {
                    rotationZ = rotation.value
                }
        )
        //唱片边框
        Image(
            painter = painterResource(id = R.drawable.bet),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.Center)
                .matchParentSize()
                .aspectRatio(1.0f)
                .padding(10.dp),
        )
        //唱片针
        Image(
            painter = painterResource(id = R.drawable.bgm),
            contentDescription = null,
            modifier = Modifier
                .align(BiasAlignment(0.3f, -1f))
                .graphicsLayer {
                    rotationZ = stylusRotation
                    transformOrigin = TransformOrigin(0f, 0f)
                }
        )
    }
}

@Composable
private fun Lyric(modifier: Modifier, lyric: List<Pair<Int, String>>, progress: Float) {
    if (lyric.isEmpty()) {
        Text(text = "暂无歌词", color = Color.White, textAlign = TextAlign.Center)
        return
    }
    val maxTime = lyric.last().first
    //TODO 还没想到更好的定位当前播放的歌词位置，先使用进度估算
    val curPosition = lyric.indexOfFirst {
        val lyricProcess = it.first * 1.0f / maxTime
        abs(progress - lyricProcess) < 0.01
    }
    val lazyListState = rememberLazyListState()
    LaunchedEffect(key1 = curPosition, block = {
        curPosition.takeIf { it >= 0 }?.let { lazyListState.scrollToItem(it) }
    })
    LazyColumn(modifier = modifier.padding(vertical = 16.dp), state = lazyListState) {
        itemsIndexed(lyric) { pos, lrcData ->
            Text(
                text = lrcData.second,
                Modifier.fillMaxWidth(),
                style = TextStyle(
                    color = if (pos == curPosition) Color.White else Color.LightGray,
                    textAlign = TextAlign.Center
                ),
            )
        }
    }
}

/**
 * 无限循环的旋转动画
 */
@Composable
private fun infiniteRotation(
    startRotate: Boolean,
    duration: Int = 15 * 1000
): Animatable<Float, AnimationVector1D> {
    var rotation by remember { mutableStateOf(Animatable(0f)) }
    LaunchedEffect(key1 = startRotate, block = {
        if (startRotate) {
            //从上次的暂停角度 -> 执行动画 -> 到目标角度（+360°）
            rotation.animateTo(
                (rotation.value % 360f) + 360f, animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = LinearEasing)
                )
            )
        } else {
            rotation.stop()
            //初始角度取余是为了防止每次暂停后目标角度无限增大
            rotation = Animatable(rotation.value % 360f)
        }
    })
    return rotation
}

private sealed class Event {
    class TrySeek(val progress: Float) : Event()

    object Seek : Event()

    object TogglePlay : Event()

    object PreviousSong : Event()

    object NextSong : Event()

    object Back : Event()

    object ToComments : Event()

    object LikeSong : Event()
}

@Preview
@Composable
fun PlaySongPreview() {
    PlaySong(song = null)
}
