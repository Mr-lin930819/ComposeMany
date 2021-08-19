package com.mrlin.composemany.model

/*********************************
 * 用户信息
 * @author mrlin
 * 创建于 2021年08月19日
 ******************************** */
data class User(
    val loginType: Int,
    val code: Int,
    val account: Account,
    val profile: Profile,
    val token: String? = null,
    val cookie: String? = null,
)

data class Account(
    val id: Int,
    val userName: String,
    val type: Int,
    val status: Int,
    val whitelistAuthority: Int,
    val createTime: Int,
    val salt: String,
    val tokenVersion: Int,
    val ban: Int,
    val baoyueVersion: Int,
    val donateVersion: Int,
    val vipType: Int,
    val viptypeVersion: Long,
    val anonimousUser: Boolean,
)

data class Profile(
    val avatarImgIdStr: String
)