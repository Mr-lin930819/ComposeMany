package com.mrlin.composemany.repository.entity

/**
 * 推荐歌单
 */
data class RecommendData(
    val code: Int,
    val featureFirst: Boolean,
    val haveRcmdSongs: Boolean,
    val recommend: List<Recommend>,
)

data class Recommend(
    val id: Long = 0,
    val type: Int = 0,
    val name: String,
    val copywriter: String? = null,
    val picUrl: String,
    val playcount: Long,
    val createTime: Long,
    val creator: Creator? = null,
    val trackCount: Int? = null,
    val userId: Long = 0,
    val alg: String? = null,
)

data class Creator(
    val remarkName: String,
    val mutual: Boolean,
    val avatarImgId: Long,
    val backgroundImgId: Long,
    val detailDescription: String,
    val defaultAvatar: Boolean,
    val expertTags: List<String>,
    val djStatus: Int,
    val followed: Boolean,
    val backgroundUrl: String,
    val backgroundImgIdStr: String,
    val avatarImgIdStr: String,
    val accountStatus: Int,
    val userId: Long,
    val vipType: Int,
    val province: Int,
    val avatarUrl: String,
    val authStatus: Int,
    val userType: Int,
    val nickname: String,
    val gender: Int,
    val birthday: Long,
    val city: Int,
    val description: String,
    val signature: String,
    val authority: Int,
)