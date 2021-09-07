package com.mrlin.composemany.repository.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 推荐歌单
 */
data class RecommendData(
    val code: Int,
    val featureFirst: Boolean,
    val haveRcmdSongs: Boolean,
    val recommend: List<Recommend>,
)

@Parcelize
data class Recommend(
    val id: Long = 0,
    val type: Int = 0,
    val name: String,
    val copywriter: String? = null,
    val picUrl: UrlString,
    val playcount: Long,
    val createTime: Long = 0,
    val creator: Creator? = null,
    val trackCount: Int? = null,
    val userId: Long = 0,
    val alg: String? = null,
) : Parcelable

@Parcelize
data class Creator(
    val remarkName: String? = null,
    val mutual: Boolean,
    val avatarImgId: Long,
    val backgroundImgId: Long,
    val detailDescription: String,
    val defaultAvatar: Boolean,
    val expertTags: List<String>? = null,
    val djStatus: Int,
    val followed: Boolean,
    val backgroundUrl: String,
    val backgroundImgIdStr: String,
    val avatarImgIdStr: String,
    val accountStatus: Int,
    val userId: Long,
    val vipType: Int,
    val province: Int,
    val avatarUrl: UrlString,
    val authStatus: Int,
    val userType: Int,
    val nickname: String,
    val gender: Int,
    val birthday: Long,
    val city: Int,
    val description: String,
    val signature: String,
    val authority: Int,
) : Parcelable

typealias UrlString = String

fun UrlString.limitSize(width: Int, height: Int = width) = "$this?param=${width}y${height}"