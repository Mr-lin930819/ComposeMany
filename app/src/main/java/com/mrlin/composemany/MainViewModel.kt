package com.mrlin.composemany

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private val _time = MutableStateFlow("")
    val time: StateFlow<String> = _time

    fun runTimer() = viewModelScope.launch {
        while (true) {
            delay(1000)
            _time.tryEmit(timeFormat.format(Date()))
        }
    }

    fun menuList() = listOf(
        MainMenu.Fund(), MainMenu.NetEaseMusic(), MainMenu.Mall()
    )

    companion object {
        val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    }
}

/**
 * 菜单图标数据来源于：
 * 1）[https://www.iconfont.cn/collections/detail?spm=a313x.7781069.1998910419.dc64b3430&cid=32207]
 * 2）[https://www.iconfont.cn/collections/detail?spm=a313x.7781069.1998910419.dc64b3430&cid=16724]
 * 3）[https://www.iconfont.cn/collections/detail?spm=a313x.7781069.1998910419.dc64b3430&cid=23172]
 */
sealed class MainMenu(val name: String, @DrawableRes val icon: Int = R.drawable.discuss) {
    class Fund : MainMenu("基金", R.drawable.discuss)
    class NetEaseMusic : MainMenu("音乐", R.drawable.music)
    class Mall : MainMenu("商城", R.drawable.mall)
}