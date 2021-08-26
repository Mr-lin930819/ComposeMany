package com.mrlin.composemany.pages.music.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mrlin.composemany.repository.NetEaseMusicApi
import com.mrlin.composemany.repository.entity.PlayList
import com.mrlin.composemany.repository.entity.Recommend
import com.mrlin.composemany.state.ViewState
import com.mrlin.composemany.utils.busyWork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.await
import javax.inject.Inject

/**
 * 歌单
 */
@HiltViewModel
class MusicPlayListViewModel @Inject constructor(
    private val musicApi: NetEaseMusicApi,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _playList: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Normal)
    val playList: StateFlow<ViewState> = _playList

    init {
        val recommend = savedStateHandle.get<Recommend>("recommend")
        busyWork(_playList) {
            val playList = musicApi.playListDetail(recommend?.id ?: throw Throwable("无歌单数据")).await().playlist
            PlayListState(playList)
        }
    }

    class PlayListState(val data: PlayList) : ViewState()
}