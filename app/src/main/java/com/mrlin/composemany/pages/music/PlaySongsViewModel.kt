package com.mrlin.composemany.pages.music

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrlin.composemany.repository.NetEaseMusicApi
import com.mrlin.composemany.repository.entity.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.updateAndGet
import retrofit2.await
import javax.inject.Inject

@HiltViewModel
class PlaySongsViewModel @Inject constructor(
    private val musicApi: NetEaseMusicApi
) : ViewModel() {
    private val _songs = MutableStateFlow(emptyList<Song>())
    private var _curIndex = MutableStateFlow(0)
    private var _mediaPlayer: MediaPlayer? = null

    val allSongs: StateFlow<List<Song>> = _songs
    var curIndex: StateFlow<Int> = _curIndex

    init {
        _mediaPlayer = MediaPlayer()
    }

    fun play() = viewModelScope.launch {
        val songId = _songs.value[curIndex.value].id
        val url = musicApi.musicUrl(songId).await().data.firstOrNull()?.url
            ?: "https://music.163.com/song/media/outer/url?id=${songId}.mp3"
        playMusic(url = url)
    }

    fun playSongs(songs: List<Song>, index: Int? = null) {
        _songs.tryEmit(songs)
        index?.let { _curIndex.tryEmit(it) }
        play()
    }

    /**
     * 下一首
     */
    private fun nextPlay() {
        _curIndex.tryEmit(_curIndex.updateAndGet { (it + 1).coerceAtMost(_songs.value.size - 1) })
        play()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun playMusic(url: String) = withContext(Dispatchers.IO) {
        try {
            _mediaPlayer?.stop()
            _mediaPlayer?.seekTo(0)
            _mediaPlayer?.setDataSource(url)
            _mediaPlayer?.prepare()
            _mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        _mediaPlayer?.release()
        _mediaPlayer = null
        super.onCleared()
    }
}