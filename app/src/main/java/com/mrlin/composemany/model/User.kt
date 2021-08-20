package com.mrlin.composemany.model

import androidx.room.*

/*********************************
 * 用户信息
 * @author mrlin
 * 创建于 2021年08月19日
 ******************************** */
@Entity(
    indices = [
        Index(value = ["ua_id"], unique = true)
    ]
)
data class User(
    val loginType: Int,
    val code: Int,
    @Embedded
    val account: Account,
    @Embedded
    val profile: Profile,
    val token: String? = null,
    val cookie: String? = null,
    @PrimaryKey
    var accountId: Int = account.id,
) {
    fun isValid() = code < 299
}

data class Account(
    @ColumnInfo(name = "ua_id")
    val id: Int,
    @ColumnInfo(name = "ua_user_name")
    val userName: String,
    @ColumnInfo(name = "ua_type")
    val type: Int,
    @ColumnInfo(name = "ua_status")
    val status: Int,
    @ColumnInfo(name = "ua_whitelist_authority")
    val whitelistAuthority: Int,
    @ColumnInfo(name = "ua_create_time")
    val createTime: Int,
    @ColumnInfo(name = "ua_salt")
    val salt: String,
    @ColumnInfo(name = "ua_token_version")
    val tokenVersion: Int,
    @ColumnInfo(name = "ua_ban")
    val ban: Int,
    @ColumnInfo(name = "ua_baoyue_version")
    val baoyueVersion: Int,
    @ColumnInfo(name = "ua_donate_version")
    val donateVersion: Int,
    @ColumnInfo(name = "ua_vip_type")
    val vipType: Int,
    @ColumnInfo(name = "ua_viptype_version")
    val viptypeVersion: Long,
    @ColumnInfo(name = "ua_anonimous_user")
    val anonimousUser: Boolean,
)

data class Profile(
    @ColumnInfo(name = "up_avatarImgIdStr")
    val avatarImgIdStr: String,
    @ColumnInfo(name = "up_nickname")
    val nickname: String,
    @ColumnInfo(name = "up_avatarUrl")
    val avatarUrl: String,
    @ColumnInfo(name = "up_signature")
    val signature: String? = null,
)