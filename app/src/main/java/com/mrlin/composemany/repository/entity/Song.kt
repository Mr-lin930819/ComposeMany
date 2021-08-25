package com.mrlin.composemany.repository.entity

data class Song(
    //歌曲id
    val id: Long,
    //歌曲名称
    val name: String? = null,
    //演唱者
    val artists: String? = null,
    //歌曲图片
    val picUrl: UString? = null,
)
