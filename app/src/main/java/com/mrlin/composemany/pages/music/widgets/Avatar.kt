package com.mrlin.composemany.pages.music.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.mrlin.composemany.repository.entity.limitSize

/*********************************
 * 头像
 * @author mrlin
 * 创建于 2021年09月08日
 ******************************** */
@OptIn(ExperimentalCoilApi::class)
@Composable
fun CircleAvatar(url: String?, size: Dp = 36.dp) {
    val sizePx = with(LocalDensity.current) { size.roundToPx() }
    Image(painter = rememberImagePainter(url?.limitSize(sizePx), builder = {
        transformations(CircleCropTransformation())
    }), contentDescription = null, modifier = Modifier.size(size = size))
}