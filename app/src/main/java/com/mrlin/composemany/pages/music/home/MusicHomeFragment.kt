package com.mrlin.composemany.pages.music.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.whenStarted
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mrlin.composemany.R
import com.mrlin.composemany.pages.music.MusicScreen
import com.mrlin.composemany.pages.music.login.MusicLogin
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
        val navController = rememberNavController()
        ComposeManyTheme {
            var navAlpha by remember { mutableStateOf(0f) }
            LaunchedEffect(key1 = true, block = {
                lifecycle.whenStarted { navAlpha = 1.0f }
            })
            NavHost(
                navController = navController,
                startDestination = HomeScreen.Home.route,
                modifier = Modifier.alpha(navAlpha)
            ) {
                composable(HomeScreen.Home.route) {
                    val userState by viewModel.userState.collectAsState()
                    Crossfade(targetState = userState) {
                        when (it) {
                            is MusicHomeState.Splash -> MusicSplash()
                            is MusicHomeState.Visitor -> MusicLogin(viewModel)
                            is MusicHomeState.Login -> MusicHome(it.user) { screen ->
                                when (screen) {
                                    is MusicScreen -> findNavController().navigate(screen.directions)
                                    is HomeScreen -> navController.navigate(screen.route)
                                }
                            }
                        }
                    }
                }
                composable(HomeScreen.DailySong.route) {
                    Text(text = "每日推荐")
                }
                composable(HomeScreen.TopList.route) {
                    Text(text = "排行榜")
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
        setContent(content = content)
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