package com.mrlin.composemany.repository.entity

/**
 * 专辑数据
 */
data class AlbumData(
    val hasMore: Boolean = false,
    val weekData: List<Album>? = null,
    val monthData: List<Album>? = null,
    val code: Int = 0
)

data class Album(
    var paid: Boolean? = null,
    var onSale: Boolean? = null,
    var mark: Int = 0,
    var artists: List<Artist>? = null,
    var copyrightId: Long = 0,
    var artist: Artist? = null,
    var picId: Long = 0L,
    var publishTime: Long = 0,
    var commentThreadId: String? = null,
    var briefDesc: String? = null,
    var picUrl: String? = null,
    var company: String? = null,
    var blurPicUrl: String? = null,
    var companyId: Long = 0L,
    var pic: Long = 0,
    var tags: String? = null,
    var status: Int = 0,
    var subType: String? = null,
    var description: String? = null,
    var name: String? = null,
    var id: Long = 0,
    var type: String? = null,
    var size: Int = 0,
    var picIdStr: String? = null,
)

data class Artist(
    var img1v1Id: Long = 0L,
    var topicPerson: Int = 0,
    var picId: Long = 0L,
    var albumSize: Int = 0,
    var musicSize: Int = 0,
    var briefDesc: String? = null,
    var followed: Boolean? = null,
    var img1v1Url: String? = null,
    var trans: String? = null,
    var picUrl: String? = null,
    var name: String? = null,
    var id: Long = 0L,
    var img1v1IdStr: String? = null,
    var transNames: List<String>? = null,
)