package com.mrlin.composemany.repository

import com.mrlin.composemany.repository.entity.AlbumData
import com.mrlin.composemany.repository.entity.MVData
import com.mrlin.composemany.repository.entity.RecommendData
import com.mrlin.composemany.repository.entity.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/*********************************
 * 网易云音乐API
 * @author mrlin
 * 创建于 2021年08月19日
 ******************************** */
interface NetEaseMusicApi {
    @GET("/login/refresh")
    fun refreshLogin(): Call<Unit>

    /**
     * 手机号登录
     */
    @GET("/login/cellphone")
    fun cellphoneLogin(
        @Query("phone") phone: String,
        @Query("password") password: String
    ): Call<User>

    /**
     * 推荐歌单
     */
    @GET("/recommend/resource")
    fun recommendResource(): Call<RecommendData>

    /**
     * 新碟上架
     */
    @GET("/top/album")
    fun topAlbums(
        @Query("limit") limit: Int = 5,
        @Query("offset") offset: Int = 0
    ): Call<AlbumData>

    /**
     * MV 排行
     */
    @GET("/top/mv")
    fun topMVs(
        @Query("limit") limit: Int = 5,
        @Query("offset") offset: Int = 0
    ): Call<MVData>
}