package com.mrlin.composemany.pages.music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.TextStyle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mrlin.composemany.R
import com.mrlin.composemany.pages.music.home.MusicHomePage
import com.mrlin.composemany.pages.music.login.MusicLoginPage
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
    private val netEaseMusicViewModel by viewModels<NetEaseMusicViewModel>()
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
            val userState by netEaseMusicViewModel.userState.collectAsState()
            val viewState by netEaseMusicViewModel.viewState.collectAsState()
            when (userState) {
                is MusicHomeState.Visitor -> MusicLoginPage(netEaseMusicViewModel)
                is MusicHomeState.Login -> MusicHomePage((userState as MusicHomeState.Login).user) {
                    findNavController().navigate(it.directions)
                }
            }
            if (viewState is ViewState.Busy) {
                Loading()
            } else if (viewState is ViewState.Error) {
                FailureTip(reason = (viewState as ViewState.Error).reason)
            }
        }
    }
}

internal fun Fragment.composeContent(content: @Composable () -> Unit): View =
    ComposeView(requireContext()).apply {
        id = R.id.chain
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