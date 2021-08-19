package com.mrlin.composemany.pages.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrlin.composemany.repository.NetEaseMusicApi
import com.mrlin.composemany.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import javax.inject.Inject

/*********************************
 * 网易云主页数据
 * @author mrlin
 * 创建于 2021年08月19日
 ******************************** */
@HiltViewModel
class NetEaseMusicViewModel @Inject constructor(private val netEaseMusicApi: NetEaseMusicApi) : ViewModel() {
    private val _userState: MutableStateFlow<MusicHomeState> = MutableStateFlow(MusicHomeState.Visitor)
    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Normal)

    val userState: StateFlow<MusicHomeState> = _userState
    val viewState: StateFlow<ViewState> = _viewState

    init {
        detectLoginState()
    }

    /**
     * 登录
     */
    fun login(phone: String, password: String) = viewModelScope.launch {
        _viewState.emit(ViewState.Busy())
        try {
            val user = netEaseMusicApi.cellphoneLogin(phone, password).awaitResponse()
                .takeIf { it.isSuccessful }?.body()
            if ((user?.code ?: 500) < 299) {
                _viewState.tryEmit(ViewState.Normal)
                _userState.emit(MusicHomeState.Login(user))
            } else {
                _viewState.tryEmit(ViewState.Error("登录失败"))
            }
        } catch (t: Throwable) {
            _viewState.tryEmit(ViewState.Error(t.message.orEmpty()))
        }
    }

    private fun detectLoginState() = viewModelScope.launch {
        _viewState.emit(ViewState.Busy())
        try {
            val response = netEaseMusicApi.refreshLogin().awaitResponse()
            _viewState.tryEmit(ViewState.Normal)
            _userState.emit(if (response.isSuccessful) MusicHomeState.Login() else MusicHomeState.Visitor)
        } catch (t: Throwable) {
            _viewState.tryEmit(ViewState.Error(t.message.orEmpty()))
        }
    }
}