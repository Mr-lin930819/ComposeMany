package com.mrlin.composemany.pages.music.playsong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.mrlin.composemany.R
import com.mrlin.composemany.pages.music.home.composeContent
import com.mrlin.composemany.pages.music.widgets.CircleAvatar
import com.mrlin.composemany.repository.entity.Comment
import com.mrlin.composemany.repository.entity.CommentData
import com.mrlin.composemany.repository.entity.CommentUser
import com.mrlin.composemany.repository.entity.Song
import com.mrlin.composemany.utils.simpleNumText
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

/**
 * 评论页
 */
@AndroidEntryPoint
class CommentsFragment : Fragment() {
    private val args by navArgs<CommentsFragmentArgs>()
    private val viewModel by viewModels<CommentsViewModel>()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composeContent {
        val song = args.song
        val commentCount by viewModel.commentCount.collectAsState()
        val commentList = viewModel.commentsPager.flow.collectAsLazyPagingItems()
        val sortType by viewModel.commentSortType.collectAsState()
        LaunchedEffect(key1 = sortType, block = { commentList.refresh() })
        ProvideTextStyle(value = MaterialTheme.typography.body2) {
            Scaffold(
                topBar = {
                    TopAppBar(title = {
                        Text(text = "评论(${commentCount})")
                    }, navigationIcon = {
                        IconButton(onClick = { findNavController().navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                    })
                }
            ) {
                Column {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        item { SongInfoArea(song) }
                        stickyHeader {
                            CommentListTitle(sortType) { viewModel.changeRankType(it) }
                        }
                        items(commentList) { CommentItem(it) }
                    }
                    Row(
                        modifier = Modifier
                            .height(48.dp)
                            .border(width = 0.5.dp, color = Color.LightGray)
                            .padding(10.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "这一次也许就是你上热评了", color = Color.LightGray)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "发送", color = Color.LightGray)
                    }
                }
            }
        }
    }

    /**
     * 评论列表标题
     */
    @Composable
    private fun CommentListTitle(
        rankType: CommentData.SortType,
        onSwitch: (CommentData.SortType) -> Unit
    ) {
        Surface {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "评论区")
                Spacer(modifier = Modifier.weight(1.0f))
                listOf(
                    "推荐" to CommentData.SortType.RECOMMEND,
                    "最热" to CommentData.SortType.HOT,
                    "最新" to CommentData.SortType.NEWEST,
                ).forEach {
                    Text(
                        text = it.first, modifier = Modifier
                            .padding(8.dp)
                            .clickable { onSwitch(it.second) },
                        fontWeight = if (rankType == it.second) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }

    /**
     * 歌曲信息区域
     */
    @Composable
    private fun SongInfoArea(song: Song) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .height(48.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            CircleAvatar(song.picUrl, size = 36.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "${song.name.orEmpty()} - ")
            Text(
                text = song.artists.orEmpty(),
                style = MaterialTheme.typography.caption
            )
        }
        Spacer(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .background(color = Color(0xFFE4E5EB))
        )
    }

    /**
     * 评论
     */
    @Composable
    private fun CommentItem(it: Comment?) {
        if (it == null) {
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .padding(8.dp)
                    .background(color = Color.LightGray)
            )
            return
        }
        val user = it.user
        val contentPaddingStart = 44.dp
        Column(modifier = Modifier.padding(8.dp)) {
            CommentTitle(comment = it, user = user)
            Text(
                text = it.content,
                modifier = Modifier.padding(end = 8.dp, start = contentPaddingStart, top = 8.dp, bottom = 8.dp)
            )
            it.showFloorComment?.replyCount?.takeIf { it > 0 }?.let {
                Text(
                    text = "${it}条回复 >",
                    color = Color(0xFF0288D1),
                    modifier = Modifier.padding(start = contentPaddingStart),
                    fontSize = 14.sp
                )
            }
            Divider(modifier = Modifier.padding(start = contentPaddingStart, top = 16.dp), thickness = 0.5.dp)
        }
    }

    /**
     * 评论title
     */
    @Composable
    private fun CommentTitle(comment: Comment, user: CommentUser) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleAvatar(user.avatarUrl, size = 36.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = user.nickname, color = Color.Gray)
                Text(
                    text = SimpleDateFormat(
                        "yyyy年MM月dd日",
                        Locale.CHINA
                    ).format(Date(comment.time)),
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }
            Spacer(modifier = Modifier.weight(1.0f))
            Text(
                text = comment.likedCount.toLong().simpleNumText(), color = Color.Gray,
                style = MaterialTheme.typography.caption
            )
            Icon(
                painter = painterResource(id = R.drawable.icon_parise),
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .size(16.dp)
            )
        }
    }
}