package com.mrlin.composemany.repository.entity

data class MusicData(
    val mvid: Long,
    val picUrl: String? = null,
    val songName: String,
    val artists: String,
    val index: Int? = null,
    val musicId: Long = 0,
)