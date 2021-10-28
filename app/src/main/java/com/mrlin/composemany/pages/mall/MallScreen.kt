package com.mrlin.composemany.pages.mall

import androidx.annotation.DrawableRes
import com.mrlin.composemany.R

sealed class MallScreen(val route: String, @DrawableRes val iconRes: Int = R.drawable.icon_song_more) {
    object Home : MallScreen("mall/home", R.drawable.icon_song_more) {
        object Main : MallScreen("mall/home/main")
    }
    object Category : MallScreen("mall/category", R.drawable.icon_song_download)
    object ShopCart : MallScreen("mall/shopCart", R.drawable.icon_song_play_type_1)
    object Mine : MallScreen("mall/mine", R.drawable.icon_dislike)

    object Detail: MallScreen("mall/detail")
}