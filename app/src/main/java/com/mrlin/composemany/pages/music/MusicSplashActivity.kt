package com.mrlin.composemany.pages.music

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mrlin.composemany.R
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MusicSplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_net_ease_music_splash)
    }
}