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
    private val _curProgress = MutableStateFlow(0f)
    private val _isPlaying = MutableStateFlow(false)
    private var tryingSeek = false

    val allSongs: StateFlow<List<Song>> = _songs
    var curIndex: StateFlow<Int> = _curIndex
    val curProgress: StateFlow<Float> = _curProgress
    val isPlaying: StateFlow<Boolean> = _isPlaying

    init {
        _mediaPlayer = MediaPlayer()
    }

    private fun play() = viewModelScope.launch {
        val songId = _songs.value[curIndex.value].id
        val url = musicApi.musicUrl(songId).await().data.firstOrNull()?.url
            ?: "https://music.163.com/song/media/outer/url?id=${songId}.mp3"
        playMusic(url = url)
        _isPlaying.tryEmit(_mediaPlayer?.isPlaying ?: false)
        queryProgress()
    }

    private fun queryProgress() = viewModelScope.launch {
        while (_mediaPlayer?.isPlaying == true) {
            if (!tryingSeek) {
                _curProgress.emit(
                    (_mediaPlayer?.currentPosition?.toFloat()
                        ?: 0f) / (_mediaPlayer?.duration?.toFloat()
                        ?: 0f)
                )
            }
            delay(1000)
        }
    }

    fun playSongs(songs: List<Song>, index: Int? = null) {
        _songs.tryEmit(songs)
        index?.let { _curIndex.tryEmit(it) }
        play()
    }

    /**
     *  暂停、恢复
     */
    fun togglePlay() {
        if (_mediaPlayer?.isPlaying != true) {
            _mediaPlayer?.start()
            queryProgress()
        } else {
            _mediaPlayer?.pause()
        }
        _isPlaying.tryEmit(_mediaPlayer?.isPlaying ?: false)
    }

    /**
     * 准备跳转到指定进度
     */
    fun trySeek(progress: Float) {
        tryingSeek = true
        _curProgress.tryEmit(progress)
    }

    /**
     * 跳转到固定进度
     */
    fun seekPlay() {
        val progress = _curProgress.value
        _mediaPlayer?.seekTo((progress * (_mediaPlayer?.duration?.toFloat() ?: 0f)).toInt())
        tryingSeek = false
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