package com.mrlin.composemany.repository.entity

/**
 * MV数据
 */
data class MVData (
    var updateTime: Long = 0,
    var data: List<MV>? = null,
    var hasMore: Boolean? = null,
    var code: Int = 0,
) {
    data class MV (
        var id: Long = 0,
        var cover: String? = null,
        var name: String? = null,
        var playCount: Long = 0,
        var briefDesc: String? = null,
        var desc: String? = null,
        var artistName: String? = null,
        var artistId: Long = 0,
        var duration: Long = 0,
        var mark: Int = 0,
        var lastRank: Int = 0,
        var score: Int = 0,
        var subed: Boolean? = null,
        var artists: List<Artist>? = null,
    )

    data class Artist (
        val id: Long = 0,
        val name: String = ""
    )
}