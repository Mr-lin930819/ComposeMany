package com.mrlin.composemany.pages.music

import androidx.navigation.NavDirections
import com.mrlin.composemany.NavGraphDirections
import com.mrlin.composemany.repository.entity.Recommend

/*********************************
 *
 * @author mrlin
 * 创建于 2021年08月23日
 ******************************** */
abstract class MusicScreen(val directions: NavDirections) {
    class PlayList(recommend: Recommend) :
        MusicScreen(NavGraphDirections.toMusicPlayListFragment(recommend))
}