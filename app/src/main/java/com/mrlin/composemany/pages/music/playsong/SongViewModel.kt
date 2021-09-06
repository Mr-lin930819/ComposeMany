package com.mrlin.composemany.pages.music.playsong

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrlin.composemany.repository.NetEaseMusicApi
import com.mrlin.composemany.repository.entity.Song
import com.mrlin.composemany.repository.entity.SongCommentData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.await
import javax.inject.Inject

/**
 * 歌曲页面
 */
@HiltViewModel
class SongViewModel @Inject constructor(private val musicApi: NetEaseMusicApi) : ViewModel() {
    private val _songComment = MutableStateFlow(SongCommentData())

    val songComment: StateFlow<SongCommentData> = _songComment

    fun loadComment(song: Song) = viewModelScope.launch {
        try {
            val comment = musicApi.songCommentData(song.id, offset = 0).await()
            _songComment.value = comment
        } catch (t: Throwable) {

        }
    }
}