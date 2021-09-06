package com.mrlin.composemany.pages.music

import androidx.navigation.NavDirections
import com.mrlin.composemany.NavGraphDirections
import com.mrlin.composemany.repository.entity.Recommend

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
}