package com.mrlin.composemany

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
        MainMenu.Fund(), MainMenu.NetEaseMusic()
    )

    companion object {
        val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    }
}

sealed class MainMenu(val name: String) {
    class Fund : MainMenu("基金")
    class NetEaseMusic : MainMenu("音乐")
}