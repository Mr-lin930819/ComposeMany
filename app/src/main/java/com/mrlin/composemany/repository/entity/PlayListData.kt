package com.mrlin.composemany.repository.entity

/**
 * 播放列表数据
 */
data class PlayListData(
    val code: Int,
    val playlist: PlayList,
)

data class PlayList(
    val tracks: List<Track>,
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