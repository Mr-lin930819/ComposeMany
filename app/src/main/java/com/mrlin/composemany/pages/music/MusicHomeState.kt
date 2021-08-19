package com.mrlin.composemany.pages.music

import com.mrlin.composemany.model.User

/*********************************
 * 音乐主页状态
 * @author mrlin
 * 创建于 2021年08月19日
 ******************************** */
sealed class MusicHomeState {
    object Visitor : MusicHomeState()
    class Login(val user: User? = null) : MusicHomeState()
}