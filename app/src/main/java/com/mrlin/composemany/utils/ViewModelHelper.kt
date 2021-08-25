package com.mrlin.composemany.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrlin.composemany.state.ViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * 进行繁忙任务
 */
internal fun ViewModel.busyWork(
    state: MutableStateFlow<ViewState>,
    work: suspend CoroutineScope.() -> Any
) = viewModelScope.launch {
    state.emit(ViewState.Busy())
    try {
        when (val result = work()) {
            is ViewState -> state.tryEmit(result)
            else -> state.tryEmit(ViewState.Normal)
        }
    } catch (t: Throwable) {
        state.tryEmit(ViewState.Error(t.message.orEmpty()))
        t.printStackTrace()
    }
}