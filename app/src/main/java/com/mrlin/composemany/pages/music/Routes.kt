package com.mrlin.composemany.pages.music

import androidx.navigation.NavDirections
import com.mrlin.composemany.NavGraphDirections
import com.mrlin.composemany.pages.music.playsong.PlaySongFragmentDirections
import com.mrlin.composemany.repository.entity.Recommend
import com.mrlin.composemany.repository.entity.Song

/*********************************
 * 音乐功能界面路由
 * @author mrlin
 * 创建于 2021年08月23日
 ******************************** */
abstract class MusicScreen(val directions: NavDirections) {
    //歌单列表
    class PlayList(recommend: Recommend) :
        MusicScreen(NavGraphDirections.toMusicPlayListFragment(recommend))

    //歌曲播放
    class PlaySong : MusicScreen(NavGraphDirections.toPlaySongFragment())

    //歌曲评论
    class SongComment(song: Song) :
        MusicScreen(PlaySongFragmentDirections.actionPlaySongFragmentToCommentsFragment(song))
}