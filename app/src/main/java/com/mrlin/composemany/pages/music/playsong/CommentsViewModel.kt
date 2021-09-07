package com.mrlin.composemany.pages.music.playsong

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrlin.composemany.repository.NetEaseMusicApi
import com.mrlin.composemany.repository.entity.Comment
import com.mrlin.composemany.repository.entity.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.await
import javax.inject.Inject

/**
 * 评论列表
 */
@HiltViewModel
class CommentsViewModel @Inject constructor(private val musicApi: NetEaseMusicApi) : ViewModel() {
    private val _comments = MutableStateFlow(emptyList<Comment>())

    val comments: StateFlow<List<Comment>> = _comments

    fun loadComment(song: Song) = viewModelScope.launch {
        try {
            val comment = musicApi.songCommentData(song.id, offset = 0, limit = 20).await()
            _comments.value = comment.comments
        } catch (t: Throwable) {

        }
    }
}