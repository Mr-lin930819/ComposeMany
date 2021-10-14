package com.mrlin.composemany.pages.music.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.BlurTransformation
import coil.transform.RoundedCornersTransformation
import com.mrlin.composemany.R
import com.mrlin.composemany.pages.music.MusicScreen
import com.mrlin.composemany.pages.music.PlaySongsViewModel
import com.mrlin.composemany.pages.music.home.composeContent
import com.mrlin.composemany.pages.music.widgets.CircleAvatar
import com.mrlin.composemany.pages.music.widgets.MiniButton
import com.mrlin.composemany.pages.music.widgets.PlayListCover
import com.mrlin.composemany.pages.music.widgets.PlayWidget
import com.mrlin.composemany.repository.entity.MusicData
import com.mrlin.composemany.repository.entity.Recommend
import com.mrlin.composemany.repository.entity.Song
import com.mrlin.composemany.repository.entity.limitSize
import com.mrlin.composemany.state.ViewState
import com.mrlin.composemany.ui.theme.ComposeManyTheme
import com.mrlin.composemany.ui.theme.LightGray
import dagger.hilt.android.AndroidEntryPoint
import me.onebone.toolbar.*

/*********************************
 * 音乐播放列表
 * @author mrlin
 * 创建于 2021年08月23日
 ******************************** */
@AndroidEntryPoint
class MusicPlayListFragment : Fragment() {
    private val args: MusicPlayListFragmentArgs by navArgs()
    private val viewModel by viewModels<MusicPlayListViewModel>()
    private val playSongsViewModel by activityViewModels<PlaySongsViewModel>()

    @OptIn(ExperimentalFoundationApi::class)
    @Suppress("UnnecessaryVariable")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composeContent {
        ComposeManyTheme {
            val recommend = args.recommend
            val playList by viewModel.playList.collectAsState()
            val expandedHeight = 240.dp
//            PlayListAppBar(recommend.name, expandedHeight = expandedHeight, background = {
//                AppBarBackground(recommend = recommend)
//            }, bottom = {
//                MusicListHeader {
//                    //播放全部
//                }
//            }) {
//                Column {
//                    Box(modifier = Modifier.weight(1.0f)) {
//                        SongsList(playList = playList, playSongsViewModel = playSongsViewModel, expandedHeight)
//                    }
//                    PlayWidget(viewModel = playSongsViewModel)
//                }
//            }

            val state = rememberCollapsingToolbarScaffoldState()
            Column {
                CollapsingToolbarScaffold(
                    modifier = Modifier.weight(1.0f),
                    state = state,
                    scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
                    toolbar = {
                        ProvideTextStyle(value = TextStyle(color = Color.White)) {
                            ToolBar(expandedHeight, state, recommend)
                        }
                    }
                ) {
                    SongsList(playList, playSongsViewModel)
                }
                PlayWidget(playSongsViewModel) { findNavController().navigate(MusicScreen.PlaySong().directions) }
            }

        }
    }

    @Composable
    private fun CollapsingToolbarScope.ToolBar(
        expandedHeight: Dp,
        state: CollapsingToolbarScaffoldState,
        recommend: Recommend
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(expandedHeight)
            .graphicsLayer {
                translationY = -(state.toolbarState.maxHeight - state.toolbarState.height)
                    .coerceAtMost(
                        (expandedHeight - 48.dp).roundToPx()
                    )
                    .toFloat()
            }) {
            AppBarBackground(recommend = recommend)
        }
        Row(
            modifier = Modifier
                .height(48.dp)
                .road(
                    Alignment.TopStart, Alignment.TopStart
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { findNavController().navigateUp() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = if (state.toolbarState.progress == 0f) recommend.name else "歌单™")
        }
    }
}

/**
 * 标题栏内容背景
 */
@OptIn(ExperimentalCoilApi::class)
@Composable
private fun BoxScope.AppBarBackground(recommend: Recommend) {
    val context = LocalContext.current
    Image(
        painter = rememberImagePainter(
            ImageRequest.Builder(context)
                .data(recommend.picUrl.limitSize(160))
                .diskCachePolicy(CachePolicy.DISABLED)
                .transformations(BlurTransformation(context, radius = 16f))
                .build()
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
        colorFilter = ColorFilter.tint(Color.Gray.copy(alpha = 0.5f), blendMode = BlendMode.Darken)
    )
    Row(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(8.dp)
    ) {
        PlayListCover(
            url = recommend.picUrl,
            playCount = recommend.playcount
        )
        Column(
            Modifier
                .padding(start = 10.dp)
                .weight(1.0f)
        ) {
            Text(text = recommend.name, maxLines = 2)
            Row {
                CircleAvatar(url = recommend.creator?.avatarUrl, size = 16.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = recommend.creator?.nickname.orEmpty(),
                    color = LightGray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun SongsList(
    playList: ViewState,
    playSongsViewModel: PlaySongsViewModel,
    topPadding: Dp = 0.dp
) {
    val curPlaySong by playSongsViewModel.curSong.collectAsState()
    when (playList) {
        is ViewState.Busy -> Box(
            modifier = Modifier
                .padding(top = topPadding)
                .fillMaxSize()
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        is MusicPlayListViewModel.PlayListState ->
            LazyColumn(contentPadding = PaddingValues(top = topPadding)) {
                stickyHeader {
                    MusicListHeader {
                        //播放全部
                    }
                }
                itemsIndexed(playList.data.tracks) { index, track ->
                    MusicListItem(
                        MusicData(
                            track.mv, index = index + 1, songName = track.name,
                            artists = "${
                                track.ar.joinToString("/") { it.name }
                            } - ${track.al.name}",
                            musicId = track.id
                        ),
                        curSongId = curPlaySong?.id ?: 0
                    ) {
                        playSongsViewModel.playSongs(
                            playList.data.tracks
                                .map {
                                    Song(
                                        it.id,
                                        it.name,
                                        it.artists(),
                                        it.al.picUrl.orEmpty()
                                    )
                                }, index
                        )
                    }
                }
            }
        is ViewState.Error -> Box(modifier = Modifier.padding(top = topPadding)) {
            Text(text = playList.reason, modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun MusicListItem(musicData: MusicData, curSongId: Long, onTap: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .height(64.dp)
            .fillMaxWidth()
            .clickable(onTap != null) { onTap?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        musicData.picUrl?.let {
            Image(
                painter = rememberImagePainter(
                    it.limitSize(100),
                    builder = {
                        transformations(RoundedCornersTransformation(5f))
                        diskCachePolicy(CachePolicy.DISABLED)
                    }
                ),
                contentDescription = null,
            )
        }
        musicData.index?.let {
            //正在播放的显示图标，其他显示顺序数字
            if (curSongId == musicData.musicId) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_rank),
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                Text(
                    text = it.toString(),
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(Modifier.weight(1f)) {
            Text(text = musicData.songName, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = musicData.artists, maxLines = 1, style = MaterialTheme.typography.caption)
        }
        MiniButton(iconRes = R.drawable.icon_event_video_b_play, modifier = Modifier.size(20.dp))
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}

@Composable
private fun MusicListHeader(
    count: Int? = null,
    onTap: (() -> Unit)? = null,
    tail: @Composable () -> Unit
) {
    Box(modifier = Modifier
        .height(48.dp)
        .background(color = Color.White)
        .clickable(onTap != null) { onTap?.invoke() }) {
        Row(
            Modifier
                .padding(start = 10.dp)
                .align(Alignment.Center), verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.play_all),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "播放全部", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(5.dp))
            count?.let { Text(text = "共${count}首", style = MaterialTheme.typography.caption) }
            Spacer(modifier = Modifier.fillMaxWidth())
            tail()
        }
    }
}