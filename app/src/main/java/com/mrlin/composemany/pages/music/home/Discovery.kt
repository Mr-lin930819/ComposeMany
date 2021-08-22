package com.mrlin.composemany.pages.music.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mrlin.composemany.R
import com.mrlin.composemany.pages.music.widgets.PlayListWidget
import com.mrlin.composemany.repository.entity.Recommend

/**
 * 【发现】页
 */
@Composable
internal fun Discovery(
    recommendList: List<Recommend>,
    onToPlayList: ((Recommend) -> Unit)? = null
) {
    Column {
        CategoryList()
        Text(text = "推荐歌单", modifier = Modifier.padding(horizontal = 10.dp))
        Box(
            modifier = Modifier.height(200.dp)
        ) { RecommendPlayList(recommendList) }
    }
}

/**
 * 分类列表
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryList() {
    val menus = mapOf(
        "每日推荐" to R.drawable.icon_daily,
        "歌单" to R.drawable.icon_playlist,
        "排行榜" to R.drawable.icon_rank,
        "电台" to R.drawable.icon_radio,
        "直播" to R.drawable.icon_look,
    )
    LazyVerticalGrid(
        modifier = Modifier.wrapContentHeight(),
        cells = GridCells.Fixed(5),
        content = {
            items(menus.entries.toList()) {
                Column(
                    Modifier
                        .aspectRatio(1 / 1.3f)
                        .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.radialGradient(
                                    listOf(Color(0xFFFF8174), Color.Red),
                                    radius = 1.0f
                                ),
                                shape = RoundedCornerShape(50)
                            )
                            .border(
                                width = 0.5.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(50)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(painter = painterResource(id = it.value), contentDescription = null)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = it.key, style = MaterialTheme.typography.caption)
                }
            }
        })
}

/**
 * 推荐歌单
 */
@Composable
private fun RecommendPlayList(recommendList: List<Recommend>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(recommendList) {
            PlayListWidget(
                text = it.name, picUrl = it.picUrl, playCount = it.playcount, maxLines = 2
            ) {

            }
        }
    }
}