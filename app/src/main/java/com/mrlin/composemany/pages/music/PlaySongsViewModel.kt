package com.mrlin.composemany.pages.music

import android.media.MediaPlayer
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrlin.composemany.MusicSettings
import com.mrlin.composemany.repository.NetEaseMusicApi
import com.mrlin.composemany.repository.entity.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await
import retrofit2.awaitResponse
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PlaySongsViewModel @Inject constructor(
    private val musicApi: NetEaseMusicApi,
    private val musicSettingsStore: DataStore<MusicSettings>
) : ViewModel() {
    private val _songs = MutableStateFlow(emptyList<Song>())
    private var _curIndex = MutableStateFlow(0)
    private var _mediaPlayer: MediaPlayer? = null
    private val _curProgress = MutableStateFlow(0f)
    private val _isPlaying = MutableStateFlow(false)
    private var tryingSeek = false
    private val _likeList = MutableStateFlow(mutableListOf<Long>())
    private val _curSong = _curIndex.map { allSongs.value.getOrNull(it) }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), null
    )

    val allSongs: StateFlow<List<Song>> = _songs
    val curSong: StateFlow<Song?> = _curSong
    val curProgress: StateFlow<Float> = _curProgress
    val isPlaying: StateFlow<Boolean> = _isPlaying
    val likeList: StateFlow<List<Long>> = _likeList

    init {
        _mediaPlayer = MediaPlayer()
        viewModelScope.launch {
            //载入喜欢的音乐id列表
            val uid = musicSettingsStore.data.map { it.userAccountId.toLong() }.firstOrNull()
            try {
                val likeList = musicApi.likeList(uid ?: 0L).await()
                _likeList.value = ArrayList(likeList.ids)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    private fun play() = viewModelScope.launch {
        val songId = _songs.value[_curIndex.value].id
        try {
            val url = musicApi.musicUrl(songId).await().data.firstOrNull()?.url
                ?: "https://music.163.com/song/media/outer/url?id=${songId}.mp3"
            playMusic(url = url)
            _isPlaying.value = _mediaPlayer?.isPlaying ?: false
            queryProgress()
        } catch (ioe: IOException) {
            nextPlay()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
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
        _isPlaying.tryEmit(false)
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
     * 上一首
     */
    fun prevPlay() {
        _curIndex.value = _curIndex.updateAndGet { (it - 1).coerceAtLeast(0) }
        play()
    }

    /**
     * 下一首
     */
    fun nextPlay() {
        _curIndex.value = _curIndex.updateAndGet { (it + 1).coerceAtMost(_songs.value.size - 1) }
        play()
    }

    /**
     * 喜欢/不喜欢歌曲
     */
    fun toggleLike() = viewModelScope.launch {
        try {
            val song = _curSong.value ?: return@launch
            val like = _likeList.value.contains(song.id)
            val response = musicApi.likeSong(song.id, !like).await()
            if (response.code == 200) {
                _likeList.value = _likeList.value.apply {
                    if (like) {
                        remove(song.id)
                    } else {
                        add(song.id)
                    }
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun playMusic(url: String) = withContext(Dispatchers.IO) {
        _mediaPlayer?.setOnCompletionListener(null)
        _mediaPlayer?.stop()
        _mediaPlayer?.seekTo(0)
        _mediaPlayer?.setDataSource(url)
        _mediaPlayer?.prepare()
        _mediaPlayer?.start()
        _mediaPlayer?.setOnCompletionListener { nextPlay() }
    }

    override fun onCleared() {
        _mediaPlayer?.release()
        _mediaPlayer = null
        super.onCleared()
    }
}