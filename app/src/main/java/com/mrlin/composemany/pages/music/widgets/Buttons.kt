package com.mrlin.composemany.pages.music.widgets

import androidx.annotation.DrawableRes
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

/**
 * 界面操作的小按键
 */
@Composable
fun MiniButton(
    @DrawableRes iconRes: Int,
    modifier: Modifier = Modifier,
    tint: Color = Color.LightGray,
    onClick: () -> Unit = { }
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = tint,
        )
    }
}