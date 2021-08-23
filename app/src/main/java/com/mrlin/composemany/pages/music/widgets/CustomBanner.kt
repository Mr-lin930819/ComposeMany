package com.mrlin.composemany.pages.music.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

/*********************************
 * 自定义banner
 * @author mrlin
 * 创建于 2021年08月23日
 ******************************** */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun CustomBanner(urls: List<String>, height: Int, onTap: (Int) -> Unit) {
    val pagerState = rememberPagerState(pageCount = urls.size)
    Box(
        Modifier
            .height(height.dp)
            .fillMaxWidth()) {
        HorizontalPager(state = pagerState) { page ->
            Image(
                painter = rememberImagePainter(urls[page]),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onTap(page) }
            )
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            activeColor = Color.White, inactiveColor = Color.LightGray
        )
    }
}