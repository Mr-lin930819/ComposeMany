package com.mrlin.composemany.repository.entity

/*********************************
 * 音乐地址
 * @author mrlin
 * 创建于 2021年08月26日
 ******************************** */
data class MusicUrlData(
    val data: List<MusicUrl>,
)

data class MusicUrl(
    val url: UrlString,
)