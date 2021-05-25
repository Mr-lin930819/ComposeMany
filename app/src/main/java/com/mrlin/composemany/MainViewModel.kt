package com.mrlin.composemany

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private val _time = MutableLiveData<String>()
    val time: LiveData<String> = _time

    fun runTimer() = viewModelScope.launch {
        while (true) {
            delay(1000)
            _time.value = timeFormat.format(Date())
        }
    }

    companion object {
        val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    }
}