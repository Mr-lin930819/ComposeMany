package com.mrlin.composemany.repository.entity

import java.util.regex.Pattern

data class LyricData(
    val lrc: Lrc,
) {
    data class Lrc(
        val version: Int,
        val lyric: String,
    ) {
        /**
         * 解析歌词为列表数据
         */
        fun parseToList(): List<Pair<Int, String>> {
            val lyrics = mutableListOf<Pair<Int, String>>()
            val pattern = Pattern.compile("\\[(\\d{1,2}):(\\d{1,2}).(\\d{1,2})](.+)")
            val matcher = pattern.matcher(lyric)
            while (matcher.find()) {
                val min = matcher.group(1)?.toIntOrNull() ?: 0
                val sec = matcher.group(2)?.toIntOrNull() ?: 0
                val mill = matcher.group(3)?.toIntOrNull() ?: 0
                val time = min * 60 * 1000 + sec * 1000 + mill * 10
                val text = matcher.group(4).orEmpty()
                lyrics.add(time to text)
            }
            lyrics.sortBy { it.first }
            return lyrics
        }
    }
}
