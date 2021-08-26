package com.mrlin.composemany.pages.music.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.BlurTransformation
import coil.transform.RoundedCornersTransformation
import com.mrlin.composemany.pages.music.PlaySongsViewModel
import com.mrlin.composemany.pages.music.home.composeContent
import com.mrlin.composemany.pages.music.widgets.PlayListCover
import com.mrlin.composemany.pages.music.widgets.PlayWidget
import com.mrlin.composemany.repository.entity.*
import com.mrlin.composemany.state.ViewState
import com.mrlin.composemany.ui.theme.ComposeManyTheme
import dagger.hilt.android.AndroidEntryPoint

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

    @Suppress("UnnecessaryVariable")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composeContent {
        ComposeManyTheme {
            val recommend = args.recommend
            val playList by viewModel.playList.collectAsState()
            val expandedHeight = 320.dp
            PlayListAppBar(recommend.name, expandedHeight = expandedHeight, background = {
                AppBarBackground(recommend = recommend)
            }, bottom = {
                MusicListHeader {
                    //播放全部
                }
            }) {
                Column {
                    Box(modifier = Modifier.weight(1.0f)) {
                        SongsList(playList = playList, playSongsViewModel = playSongsViewModel, expandedHeight)
                    }
                    PlayWidget(viewModel = playSongsViewModel)
                }
            }
        }
    }
}

/**
 * 标题栏内容背景
 */
@Composable
private fun BoxScope.AppBarBackground(recommend: Recommend) {
    val context = LocalContext.current
    Image(
        painter = rememberImagePainter(
            ImageRequest.Builder(context)
                .data(recommend.picUrl.limitSize(160))
                .diskCachePolicy(CachePolicy.DISABLED)
                .transformations(BlurTransformation(context, radius = 20f))
                .build()
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
    Row(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(8.dp)
    ) {
        PlayListCover(
            url = recommend.picUrl,
            width = 160f,
            playCount = recommend.playcount
        )
        Column(
            Modifier
                .padding(start = 10.dp)
                .weight(1.0f)
        ) {
            Text(text = recommend.name, maxLines = 2)
            Row {
                Image(
                    painter = rememberImagePainter(
                        recommend.creator?.avatarUrl?.limitSize(
                            50
                        ).orEmpty()
                    ),
                    contentDescription = null,
                )
                Text(text = recommend.creator?.nickname.orEmpty())
            }
        }
    }
}

@Composable
private fun SongsList(playList: ViewState, playSongsViewModel: PlaySongsViewModel, topPadding: Dp = 0.dp) {
    when (playList) {
        is ViewState.Busy -> Box(modifier = Modifier.padding(top = topPadding)) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        is MusicPlayListViewModel.PlayListState ->
            LazyColumn(contentPadding = PaddingValues(top = topPadding)) {
                itemsIndexed(playList.data.tracks) { index, track ->
                    MusicListItem(
                        MusicData(
                            track.mv, index = index + 1, songName = track.name,
                            artists = "${
                                track.ar.joinToString("/") { it.name }
                            } - ${track.al.name}"
                        )
                    ) {
                        playSongsViewModel.playSongs(
                            playList.data.tracks
                                .map {
                                    Song(
                                        it.id,
                                        it.name,
                                        it.artists(),
                                        UString(it.al.picUrl.orEmpty())
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
private fun MusicListItem(musicData: MusicData, onTap: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .height(80.dp)
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
            Text(
                text = it.toString(),
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )
        }
        Column {
            Text(text = musicData.songName, maxLines = 1)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = musicData.artists, maxLines = 1)
        }
    }
}

/**
 * 关于内嵌滚动实现折叠工具栏，参考的官网例子：
 * [https://developer.android.google.cn/reference/kotlin/androidx/compose/ui/input/nestedscroll/package-summary]
 */
@Composable
private fun PlayListAppBar(
    title: String, expandedHeight: Dp? = null, bottom: (@Composable () -> Unit)? = null,
    background: (@Composable BoxScope.() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    val toolbarHeight = 48.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val expandedHeightPx =
        with(LocalDensity.current) { expandedHeight?.roundToPx()?.toFloat() ?: toolbarHeightPx }
    val toolbarExpandedHeightPx = remember { mutableStateOf(expandedHeightPx) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.value + delta
                val newExpendedHeight = toolbarExpandedHeightPx.value + delta
                toolbarExpandedHeightPx.value =
                    newExpendedHeight.coerceIn(toolbarHeightPx, expandedHeightPx)
                toolbarOffsetHeightPx.value = newOffset.coerceIn(-expandedHeightPx, 0f)
                return Offset.Zero
            }
        }
    }
    val percent = toolbarOffsetHeightPx.value.plus(expandedHeightPx) / expandedHeightPx
    val tbHeight = with(LocalDensity.current) { (toolbarExpandedHeightPx.value).toDp() }
    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        content?.invoke()
        Surface(
            color = MaterialTheme.colors.primary,
            modifier = Modifier
//                .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) },
        ) {
            Box {
                background?.let {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .alpha(percent), content = it
                    )
                }
                Column(modifier = Modifier
                    .height(tbHeight)
                    .fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        //标题栏
                        Text(
                            title,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(1 - percent)
                        )
                    }
                    Box(modifier = Modifier.weight(1.0f))
                    //底栏
                    bottom?.invoke()
                }
            }
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
        .height(64.dp)
        .clickable(onTap != null) { onTap?.invoke() }) {
        Row(
            Modifier
                .padding(start = 10.dp)
                .align(Alignment.Center), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(36.dp, 36.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "播放全部")
            Spacer(modifier = Modifier.width(5.dp))
            count?.let { Text(text = "共${count}首") }
            Spacer(modifier = Modifier.fillMaxWidth())
            tail()
        }
    }
}