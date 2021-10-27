package com.mrlin.composemany.repository.entity

import com.google.gson.annotations.SerializedName

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
    val coverImgUrl: String = "",
    val trackCount: Int = 0,
    val id: Long = 0,
    val playCount: Long = 0,
) {
    enum class Op {
        @SerializedName("add")
        ADD,
        @SerializedName("del")
        DEL
    }
}

data class Subscribers(
    val userId: Long,
)

data class Track(
    val name: String,
    val id: Long,
    val mv: Long,
    val ar: List<Ar>,
    val al: Al,
    val t: Int = 0,
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