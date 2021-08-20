package com.mrlin.composemany.pages.music

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrlin.composemany.MusicSettings
import com.mrlin.composemany.model.User
import com.mrlin.composemany.repository.NetEaseMusicApi
import com.mrlin.composemany.repository.db.MusicDatabase
import com.mrlin.composemany.state.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import javax.inject.Inject

/*********************************
 * 网易云主页数据
 * @author mrlin
 * 创建于 2021年08月19日
 ******************************** */
@HiltViewModel
class NetEaseMusicViewModel @Inject constructor(
    private val netEaseMusicApi: NetEaseMusicApi,
    private val musicDb: MusicDatabase,
    private val musicSettings: DataStore<MusicSettings>
) : ViewModel() {
    private val _userState: MutableStateFlow<MusicHomeState> = MutableStateFlow(MusicHomeState.Visitor)
    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Normal)

    val userState: StateFlow<MusicHomeState> = _userState
    val viewState: StateFlow<ViewState> = _viewState

    init {
        doBusyWork {
            val userAccountId = musicSettings.data.firstOrNull()?.userAccountId
            if (userAccountId != null) {
                //已登录用户，则直接进入已登录状态
                musicDb.userDao().findUser(userAccountId.toLong())?.run {
                    _userState.emit(MusicHomeState.Login(user = this))
                    return@doBusyWork
                }
            }
            val response = netEaseMusicApi.refreshLogin().awaitResponse()
            _userState.emit(if (response.isSuccessful) MusicHomeState.Login() else MusicHomeState.Visitor)
        }
    }

    /**
     * 登录
     */
    fun login(phone: String, password: String) = doBusyWork {
        val user = netEaseMusicApi.cellphoneLogin(phone, password).awaitResponse().takeIf { it.isSuccessful }?.body()
        user?.takeIf { it.isValid() }?.run {
            //保存已登录用户
            user.accountId = user.account.id
            musicSettings.updateData { it.toBuilder().setUserAccountId(user.accountId).build() }
            musicDb.userDao().insert(user = user)
            _userState.emit(MusicHomeState.Login(user))
        } ?: throw Throwable("登录失败")
    }

    /**
     * 进行繁忙任务
     */
    private fun doBusyWork(work: suspend CoroutineScope.() -> Unit) = viewModelScope.launch {
        _viewState.emit(ViewState.Busy())
        try {
            work()
            _viewState.tryEmit(ViewState.Normal)
        } catch (t: Throwable) {
            _viewState.tryEmit(ViewState.Error(t.message.orEmpty()))
        }
    }
}

/*********************************
 * 音乐主页状态
 * @author mrlin
 * 创建于 2021年08月19日
 ******************************** */
sealed class MusicHomeState {
    object Visitor : MusicHomeState()
    class Login(val user: User? = null) : MusicHomeState()
}