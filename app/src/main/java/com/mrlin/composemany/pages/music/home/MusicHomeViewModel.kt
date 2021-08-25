package com.mrlin.composemany.pages.music.home

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import com.mrlin.composemany.MusicSettings
import com.mrlin.composemany.repository.NetEaseMusicApi
import com.mrlin.composemany.repository.db.MusicDatabase
import com.mrlin.composemany.repository.entity.*
import com.mrlin.composemany.state.ViewState
import com.mrlin.composemany.utils.busyWork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.await
import retrofit2.awaitResponse
import javax.inject.Inject

/*********************************
 * 网易云主页数据
 * @author mrlin
 * 创建于 2021年08月19日
 ******************************** */
@HiltViewModel
class MusicHomeViewModel @Inject constructor(
    private val netEaseMusicApi: NetEaseMusicApi,
    private val musicDb: MusicDatabase,
    private val musicSettings: DataStore<MusicSettings>
) : ViewModel() {
    private val _userState: MutableStateFlow<MusicHomeState> =
        MutableStateFlow(MusicHomeState.Splash)
    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Normal)
    private val _discoveryData: MutableStateFlow<DiscoveryViewData> =
        MutableStateFlow(DiscoveryViewData())

    val userState: StateFlow<MusicHomeState> = _userState
    val viewState: StateFlow<ViewState> = _viewState
    val discoveryData: StateFlow<DiscoveryViewData> = _discoveryData

    init {
        busyWork(_viewState) {
            val userAccountId = musicSettings.data.firstOrNull()?.userAccountId
            if (userAccountId != null) {
                //已登录用户，则直接进入已登录状态
                musicDb.userDao().findUser(userAccountId.toLong())?.run {
                    _userState.emit(MusicHomeState.Login(user = this))
                    return@busyWork ViewState.Normal
                }
            }
            val response = netEaseMusicApi.refreshLogin().awaitResponse()
            _userState.emit(if (response.isSuccessful) MusicHomeState.Login() else MusicHomeState.Visitor)
        }
    }

    /**
     * 登录
     */
    fun login(phone: String, password: String) = busyWork(_viewState) {
        val user = netEaseMusicApi.cellphoneLogin(phone, password).awaitResponse()
            .takeIf { it.isSuccessful }?.body()
        user?.takeIf { it.isValid() }?.run {
            //保存已登录用户
            user.accountId = user.account.id
            musicSettings.updateData { it.toBuilder().setUserAccountId(user.accountId).build() }
            musicDb.userDao().insert(user = user)
            _userState.emit(MusicHomeState.Login(user))
        } ?: throw Throwable("登录失败")
    }

    /**
     * 发现页数据载入
     */
    fun loadDiscoveryPage() = busyWork(_viewState) {
        val bannerCall = netEaseMusicApi.banners()
        val recommendDataCall = netEaseMusicApi.recommendResource()
        val topAlbumDataCall = netEaseMusicApi.topAlbums()
        val mvListCall = netEaseMusicApi.topMVs()
        _discoveryData.tryEmit(DiscoveryViewData().apply {
            try {
                bannerList = bannerCall.await().banners.orEmpty()
            } catch (_: Throwable) {
            }
            try {
                recommendList = recommendDataCall.await().recommend
            } catch (_: Throwable) {
            }
            //新碟接口由于目前API的limit无效（无法分页返回了所有的月数据）导致数据量过大，因此暂时不使用
//            try {
//                newAlbumList = topAlbumDataCall.await().weekData.orEmpty()
//            } catch (_: Throwable) {
//            }
            try {
                topMVList = mvListCall.await().data.orEmpty()
            } catch (_: Throwable) {
            }
        })
    }
}

/**
 * 发现页数据
 */
class DiscoveryViewData {
    var bannerList: List<BannerData.Banner> = emptyList()
    var recommendList: List<Recommend> = emptyList()
    var newAlbumList: List<Album> = emptyList()
    var topMVList: List<MVData.MV> = emptyList()
}

/**
 * 音乐主页状态
 */
sealed class MusicHomeState {
    object Splash : MusicHomeState()
    object Visitor : MusicHomeState()
    class Login(val user: User? = null) : MusicHomeState()
}