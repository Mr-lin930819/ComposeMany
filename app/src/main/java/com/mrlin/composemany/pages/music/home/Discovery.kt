package com.mrlin.composemany.pages.music.home

import androidx.compose.foundation.*
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
import com.mrlin.composemany.pages.music.DiscoveryViewData
import com.mrlin.composemany.pages.music.HomeScreen
import com.mrlin.composemany.pages.music.MusicScreen
import com.mrlin.composemany.pages.music.widgets.CustomBanner
import com.mrlin.composemany.pages.music.widgets.PlayListWidget
import com.mrlin.composemany.repository.entity.Album
import com.mrlin.composemany.repository.entity.MVData
import com.mrlin.composemany.repository.entity.Recommend

/**
 * 【发现】页
 */
@Composable
internal fun Discovery(
    discoveryViewData: DiscoveryViewData,
    onToScreen: ((Any) -> Unit)? = null
) {
    Column {
        CustomBanner(urls = discoveryViewData.bannerList.map { it.pic.orEmpty() }, height = 140) {

        }
        CategoryList { menuName ->
            when (menuName) {
                "每日推荐" -> onToScreen?.invoke(HomeScreen.DailySong)
                "排行榜" -> onToScreen?.invoke(HomeScreen.TopList)
            }
        }
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Text(text = "推荐歌单", modifier = Modifier.padding(10.dp))
            Box(modifier = Modifier.height(200.dp)) {
                RecommendPlayList(discoveryViewData.recommendList) {
                    onToScreen?.invoke(MusicScreen.PlayList(it))
                }
            }
            discoveryViewData.newAlbumList.takeIf { it.isNotEmpty() }?.let {
                Text(text = "新碟上架", modifier = Modifier.padding(10.dp))
                Box(modifier = Modifier.height(200.dp)) { NewAlbumList(it) }
            }
            discoveryViewData.topMVList.takeIf { it.isNotEmpty() }?.let {
                Text(text = "MV 排行", modifier = Modifier.padding(10.dp))
                Box(modifier = Modifier.height(200.dp)) { TopMvList(it) }
            }
        }
    }
}

/**
 * 分类列表
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CategoryList(onClick: (String) -> Unit) {
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
                        .padding(8.dp)
                        .clickable { onClick(it.key) },
                    horizontalAlignment = Alignment.CenterHorizontally
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
private fun RecommendPlayList(recommendList: List<Recommend>, onClick: (Recommend) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(recommendList) {
            PlayListWidget(
                text = it.name, picUrl = it.picUrl, playCount = it.playcount, maxLines = 2
            ) {
                onClick(it)
            }
        }
    }
}

/**
 * 新碟上架
 */
@Composable
private fun NewAlbumList(albumList: List<Album>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(albumList) {
            PlayListWidget(
                text = it.name.orEmpty(), picUrl = it.picUrl, subText = it.artist?.name.orEmpty(),
                maxLines = 2
            ) {

            }
        }
    }
}

/**
 * MV 排行
 */
@Composable
private fun TopMvList(mvList: List<MVData.MV>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(10.dp)
    ) {
        items(mvList) {
            PlayListWidget(
                text = it.name.orEmpty(), picUrl = it.cover.orEmpty(),
                subText = it.artistName.orEmpty(), maxLines = 2
            ) {

            }
        }
    }
}