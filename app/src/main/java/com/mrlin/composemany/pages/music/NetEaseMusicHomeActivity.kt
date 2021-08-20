package com.mrlin.composemany.pages.music

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.mrlin.composemany.state.ViewState
import com.mrlin.composemany.ui.theme.Blue500
import com.mrlin.composemany.ui.theme.ComposeManyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NetEaseMusicSplashActivity : AppCompatActivity() {
    private val netEaseMusicViewModel by viewModels<NetEaseMusicViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                    is MusicHomeState.Login -> MusicHomePage((userState as MusicHomeState.Login).user)
                }
                if (viewState is ViewState.Busy) {
                    Loading()
                } else if (viewState is ViewState.Error) {
                    FailureTip(reason = (viewState as ViewState.Error).reason)
                }
            }
        }
    }
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
        Text(text = reason, modifier = Modifier.align(Alignment.Center), style = TextStyle(color = Color.White))
    }
}