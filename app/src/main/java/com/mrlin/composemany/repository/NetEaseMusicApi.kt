package com.mrlin.composemany.repository

import com.mrlin.composemany.repository.entity.*
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

    /**
     * banner数据
     */
    @GET("/banner")
    fun banners(@Query("type") type: Int = BannerData.TYPE_ANDROID): Call<BannerData>

    /**
     * 播放列表详情
     */
    @GET("/playlist/detail")
    fun playListDetail(@Query("id") id: Long): Call<PlayListData>

    /**
     * 获取音乐 url
     */
    @GET("/song/url")
    fun musicUrl(@Query("id") id: Long, @Query("br") br: Int = 128000): Call<MusicUrlData>

    /**
     * 获取个人歌单
     */
    @GET("/user/playlist")
    fun selfPlaylistData(@Query("uid") uid: Long): Call<MyPlayListData>

    /**
     * 歌曲评论
     */
    @GET("/comment/music")
    fun songCommentData(
        @Query("id") id: Long,
        //取出评论数量 , 默认为 20
        @Query("limit") limit: Int = 20,
        //偏移数量 , 用于分页 , 如 :( 评论页数 -1)*20, 其中 20 为 limit 的值
        @Query("offset") offset: Int
    ): Call<SongCommentData>
}