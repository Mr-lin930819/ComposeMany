package com.mrlin.composemany.repository.entity

data class LyricData(
    val lrc: Lrc,
) {
    data class Lrc(
        val version: Int,
        val lyric: String,
    )
}
