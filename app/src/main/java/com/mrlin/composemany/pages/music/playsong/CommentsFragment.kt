package com.mrlin.composemany.pages.music.playsong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.mrlin.composemany.pages.music.home.composeContent
import com.mrlin.composemany.repository.entity.limitSize
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
        val comments by viewModel.comments.collectAsState()
        LaunchedEffect(key1 = viewModel, block = {
            viewModel.loadComment(song)
        })
        Scaffold {
            LazyColumn {
                item {
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .height(48.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(painter = rememberImagePainter(song.picUrl?.limitSize(96), builder = {
                            transformations(CircleCropTransformation())
                        }), contentDescription = null)
                        Text(text = "${song.name.orEmpty()} - ")
                        Text(
                            text = song.artists.orEmpty(),
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
                stickyHeader {
                    Surface {
                        Row(modifier = Modifier.padding(8.dp)) {
                            Text(text = "评论区")
                            Spacer(modifier = Modifier.weight(1.0f))
                            Text(text = "推荐")
                            Text(text = "最热")
                            Text(text = "最新")
                        }
                    }
                }
                items(comments) {
                    Row(modifier = Modifier.padding(8.dp)) {
                        val user = it.user
                        Image(
                            painter = rememberImagePainter(
                                user.avatarUrl?.limitSize(72),
                                builder = {
                                    transformations(CircleCropTransformation())
                                }),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = SimpleDateFormat(
                                        "yyyy年MM月dd日",
                                        Locale.CHINA
                                    ).format(Date(it.time)),
                                    style = MaterialTheme.typography.caption
                                )
                                Text(text = it.likedCount.toLong().simpleNumText())
                            }
                            Text(text = it.content, modifier = Modifier.padding(end = 8.dp))
                        }
                    }
                }
            }
        }
    }
}