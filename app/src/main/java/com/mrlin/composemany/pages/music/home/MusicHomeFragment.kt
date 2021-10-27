package com.mrlin.composemany.pages.music.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mrlin.composemany.R
import com.mrlin.composemany.pages.music.MusicScreen
import com.mrlin.composemany.pages.music.PlaySongsViewModel
import com.mrlin.composemany.pages.music.login.MusicLogin
import com.mrlin.composemany.pages.music.widgets.PlayWidget
import com.mrlin.composemany.state.ViewState
import com.mrlin.composemany.ui.theme.Blue500
import com.mrlin.composemany.ui.theme.ComposeManyTheme
import dagger.hilt.android.AndroidEntryPoint

/*********************************
 * 音乐主页
 * @author mrlin
 * 创建于 2021年08月23日
 ******************************** */
@AndroidEntryPoint
class NetEaseMusicHomeFragment : Fragment() {
    private val viewModel by viewModels<MusicHomeViewModel>()
    private val playSongViewModel by activityViewModels<PlaySongsViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = composeContent {
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setStatusBarColor(
                color = Blue500
            )
        }
        ComposeManyTheme {
            val userState by viewModel.userState.collectAsState()
            val playList by playSongViewModel.allSongs.collectAsState()
            Crossfade(targetState = userState) {
                when (it) {
                    is MusicHomeState.Splash -> MusicSplash()
                    is MusicHomeState.Visitor -> MusicLogin(viewModel) { requireActivity().finish() }
                    is MusicHomeState.Login -> Column {
                        MusicHome(it.user, modifier = Modifier.weight(1f)) { screen ->
                            when (screen) {
                                is MusicScreen -> findNavController().navigate(screen.directions)
                            }
                        }
                        if (playList.isNotEmpty()) {
                            //有播放歌单时显示播放控件
                            PlayWidget(playSongViewModel, height = 56.dp) {
                                findNavController().navigate(MusicScreen.PlaySong().directions)
                            }
                        }
                    }
                }
            }
            when (val state = viewModel.viewState.collectAsState().value) {
                is ViewState.Busy -> Loading()
                is ViewState.Error -> FailureTip(reason = state.reason)
            }
        }
    }
}

@Composable
private fun MusicSplash() {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.music), contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(128.dp)
        )
    }
}

internal sealed class HomeScreen(open val route: String) {
    object Home : HomeScreen("home")
    object DailySong : HomeScreen("dailySong")
    object TopList : HomeScreen("topList")
}

internal fun Fragment.composeContent(content: @Composable () -> Unit): View =
    ComposeView(requireContext()).apply {
        setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
        )
        setContent(content = {
            ComposeManyTheme(content = content)
        })
    }

@Composable
private fun Loading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}

@Composable
private fun FailureTip(reason: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Red)
    ) {
        Text(
            text = reason,
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(color = Color.White)
        )
    }
}