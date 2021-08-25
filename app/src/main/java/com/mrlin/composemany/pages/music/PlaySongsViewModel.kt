package com.mrlin.composemany.pages.music

import androidx.lifecycle.ViewModel
import com.mrlin.composemany.repository.entity.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PlaySongsViewModel @Inject constructor() : ViewModel() {
    private val _songs = MutableStateFlow(emptyList<Song>())
    private var _curIndex = MutableStateFlow(0)

    val allSongs: StateFlow<List<Song>> = _songs
    val curIndex: StateFlow<Int> = _curIndex

    fun playSongs(songs: List<Song>, index: Int? = null) {
        _songs.tryEmit(songs)
        index?.let { _curIndex.tryEmit(it) }
    }
}