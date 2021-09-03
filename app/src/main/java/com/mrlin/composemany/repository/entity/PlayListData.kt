package com.mrlin.composemany.repository.entity

/**
 * 播放列表数据
 */
data class PlayListData(
    val code: Int,
    val playlist: PlayList,
)

/**
 * 个人歌单
 */
data class MyPlayListData(
    val code: Int,
    val playlist: List<PlayList>,
)

data class PlayList(
    val tracks: List<Track>,
    val creator: Subscribers? = null,
    val name: String = "",
)

data class Subscribers(
    val userId: Int,
)

data class Track(
    val name: String,
    val id: Long,
    val mv: Long,
    val ar: List<Ar>,
    val al: Al,
) {
    fun artists() = ar.joinToString("/") { it.name }
}

data class Ar(
    val id: Long,
    val name: String,
)

data class Al(
    val id: Long,
    val name: String,
    val picUrl: String? = null,
)